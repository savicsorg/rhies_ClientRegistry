package rhies.fhir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class rhies_PatientResourceProvider implements IResourceProvider {

    FhirContext ctx = FhirContext.forR4();
    IParser parser = ctx.newJsonParser();
    static String URL;
    static int PORT;
    static String DBNAME;
    static File propertiesfile = null;

    public rhies_PatientResourceProvider() {

    }

    public DB dbConnection() throws UnknownHostException {
        getProperty();
        MongoClient mongoClient = new MongoClient(URL, PORT);
        DB database = mongoClient.getDB(DBNAME);
        return database;
    }

    private static void getProperty() {
        try {
            if (propertiesfile == null) {
                System.out.println("Stored propreties on  " + propertiesfile.getAbsolutePath());
                createProperties();
            } else {
                if (propertiesfile.exists()) {
                    InputStream input = new FileInputStream(propertiesfile.getAbsolutePath());
                    Properties prop = new Properties();

                    // load a properties file from class path, inside static method
                    prop.load(input);
                    URL = (String) prop.getProperty("db.url");
                    PORT = Integer.valueOf(prop.getProperty("db.port"));
                    DBNAME = (String) prop.getProperty("db.name");
                    input.close();

                } else {
                    System.out.println("Sorry, unable to find config.properties");
                    return;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createProperties() {
        try {
            String propertyHome = System.getenv("CATALINA_HOME");
            if (propertyHome == null) {
                propertyHome = System.getProperty("user.home");
            }

            String propertiesfilePath = propertyHome + "/" + Constants.RHIES_CLIENT_REGISTRY_FOLDER + "/" + Constants.PROPERTIES_FILE_NAME;
           
            propertiesfile = new File(propertiesfilePath);
             File propertyHomeFolder = propertiesfile.getParentFile();
            if (!propertyHomeFolder.exists()) {
                propertyHomeFolder.mkdir();
            }
            
            
            
            System.out.println("Stored propreties on  " + propertiesfile.getAbsolutePath());
            if (!propertiesfile.exists()) {
                OutputStream output = new FileOutputStream(propertiesfile);
                Properties prop = new Properties();
                prop.setProperty("db.url", Constants.DEFAULT_DB_URL);
                prop.setProperty("db.port", Constants.DEFAULT_DB_PORT);
                prop.setProperty("db.name", Constants.DEFAULT_DB_NAME);
                prop.store(output, null);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }

    /**
     * Implementation of the "read" (Get) method
     *
     * @throws IOException
     * @throws SecurityException
     */
    @Read
    public Patient read(@IdParam IdType theId) throws ResourceNotFoundException, SecurityException, IOException {
        Patient fhirPatient = new Patient();

        DBCollection patientCollection = dbConnection().getCollection("patients");

        BasicDBObject query = new BasicDBObject("id", theId.getValue().split("/")[1]);
        DBCursor cursor = patientCollection.find(query);
        DBObject dbobject = cursor.one();

        if (dbobject != null) {
            String patientData = dbobject.toString();
            fhirPatient = parser.parseResource(Patient.class, patientData);
        } else {
            utils.error("Patient with nida " + theId + " not found!");
        }

        return fhirPatient;
    }

    /**
     * Implementation of the "delete" (Get) method
     *
     * @throws UnknownHostException
     */
    @Delete
    public MethodOutcome delete(@IdParam IdType theId) throws UnknownHostException, ResourceNotFoundException {
        MethodOutcome method = new MethodOutcome();

        DBCollection patientCollection = dbConnection().getCollection("patients");
        BasicDBObject query = new BasicDBObject("id", theId.getValue().split("/")[1]);
        patientCollection.remove(query);

        method.setCreated(true);
        return method;
    }

    @Search
    public List<Patient> search(
            @OptionalParam(name = "nida") StringParam nida,
            @OptionalParam(name = "pcid") StringParam pcid,
            @OptionalParam(name = Patient.SP_FAMILY) StringParam FamilyName,
            @OptionalParam(name = Patient.SP_GIVEN) StringParam givenName,
            @OptionalParam(name = Patient.SP_GENDER) StringParam gender,
            @OptionalParam(name = Patient.SP_BIRTHDATE) StringParam birthDate,
            @OptionalParam(name = Patient.SP_ACTIVE) StringParam active,
            @OptionalParam(name = Patient.SP_IDENTIFIER) StringParam identifier
    ) throws UnknownHostException {
        BasicDBObject query = new BasicDBObject();

        if (pcid != null) {
            query.append("id", buildSearchPattern(pcid.getValue().toString()));
        }

        if (FamilyName != null) {
            query.append("name.family", buildSearchPattern(FamilyName.getValue().toString()));
        }

        if (givenName != null) {
            query.append("name.given", buildSearchPattern(givenName.getValue().toString()));
        }

        if (gender != null) {
            query.append("gender", buildSearchPattern(gender.getValue().toString()));
        }

        if (birthDate != null) {
            query.append("birthDate", buildSearchPattern(birthDate.getValue().toString()));
        }

        if (active != null) {
            query.append("active", buildSearchPattern(active.getValue().toString()));
        }

        if (identifier != null) {
            query.append("identifier.value", buildSearchPattern(identifier.getValue().toString()));
        }

        if (nida != null) {
            query.append("identifier.value", buildSearchPattern(nida.getValue().toString()));
        }

        DBCollection patientCollection = dbConnection().getCollection("patients");
        List<Patient> retVal = new ArrayList<Patient>();
        DBCursor cursor = patientCollection.find(query);

        Integer logicalId = 0;
        for (Iterator iterator = cursor.iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            String patientData = next.toString();
            Patient patient = parser.parseResource(Patient.class, patientData);
            String versionId = "1";
            patient.setId(new IdType("Patient", logicalId.toString(), versionId));
            retVal.add(patient);
            logicalId++;
        }

        return retVal;
    }

    private BasicDBObject buildSearchPattern(String value) {
        return new BasicDBObject("$regex", ".*" + value + ".*").append("$options", "i");
    }

    /**
     * Implementation of the "Create" (Post) method
     *
     * @throws IOException
     */
    @Create
    public MethodOutcome create(@ResourceParam String incomingPatient) throws NullPointerException, IOException {
        MethodOutcome method = new MethodOutcome();
        IParser par = ctx.newJsonParser();
        JsonParser parser = new JsonParser();

        //errors handling
        if (incomingPatient == null || incomingPatient.trim().equals("")) {
            utils.error(Constants.ERROR_PATIENT_EMPTY);
            return method;
        }

        Patient patient = new Patient();
        patient = par.parseResource(Patient.class, incomingPatient);
        //PCID existance
        if (patient.getId() == null || patient.getId().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_PCID);
            return method;
        }
        //NIDA existance, Notify no nida but save into CR
        if (patient.getIdentifier() == null || patient.getIdentifier().size() == 0) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_NIDA);
        } else {
            if (!patient.getIdentifier().get(0).getSystem().equals("NIDA")) {
                utils.error(patient, Constants.ERROR_PATIENT_NO_NIDA);
            }
        }
        //name existance
        if (patient.getName() == null || patient.getName().size() == (0)) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_NAME);
            return method;
        }
        //BirthDate existance
        if (patient.getBirthDate() == null) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_BIRTHDATE);
            return method;
        }
        //Gender existance
        if (patient.getGender() == null) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_GENDER);
            return method;
        }
        //fatherName existance
        if (patient.getExtensionByUrl("fatherName") == null || patient.getExtensionByUrl("fatherName").getValue() == null || patient.getExtensionByUrl("fatherName").getValue().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_FATHERNAME);
            return method;
        }

        //motherName existance
        if (patient.getExtensionByUrl("motherName") == null || patient.getExtensionByUrl("motherName").getValue() == null || patient.getExtensionByUrl("motherName").getValue().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_MOTHERNAME);
            return method;
        }

        //address existance
        if (patient.getAddress() == null || patient.getAddress().size() == 0) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_ADDRESS);
            return method;
        }

        //country existance
        if (patient.getAddress().get(0).getCountry() == null || patient.getAddress().get(0).getCountry().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_COUNTRY);
            return method;
        }

        //Province/State existance
        if (patient.getAddress().get(0).getState() == null || patient.getAddress().get(0).getState().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_PROVINCE);
            return method;
        }

        //district existance
        if (patient.getAddress().get(0).getDistrict() == null || patient.getAddress().get(0).getDistrict().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_DISTRICT);
            return method;
        }

        //city/sector existance
        if (patient.getExtensionByUrl("sector") == null || patient.getExtensionByUrl("sector").getValue() == null || patient.getExtensionByUrl("sector").getValue().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_SECTOR);
            return method;
        }

        //cell existance
        if (patient.getExtensionByUrl("cell") == null || patient.getExtensionByUrl("cell").getValue() == null || patient.getExtensionByUrl("cell").getValue().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_CELL);
            return method;
        }

        //umudugudu existance
        if (patient.getExtensionByUrl("umudugudu") == null || patient.getExtensionByUrl("umudugudu").getValue() == null || patient.getExtensionByUrl("umudugudu").getValue().equals("")) {
            utils.error(patient, Constants.ERROR_PATIENT_NO_UMUDUGUDU);
            return method;
        }

        //everything is ok,  we can save
        DBCollection patientCollection = dbConnection().getCollection("patients");
        BasicDBObject query = new BasicDBObject("id", patient.getId().split("/")[1]);
        DBCursor cursor = patientCollection.find(query);

        DBObject dbobject = cursor.one();
        String encoded = par.encodeResourceToString(patient);
        DBObject doc = (DBObject) JSON.parse(encoded);

        if (dbobject != null) {
            patientCollection.update(query, doc);
        } else {
            patientCollection.insert(doc);
        }

        method.setCreated(true);
        return method;
    }
}
