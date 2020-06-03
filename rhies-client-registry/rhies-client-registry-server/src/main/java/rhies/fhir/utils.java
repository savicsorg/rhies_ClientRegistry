/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rhies.fhir;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.time.LocalDate;
import java.time.Month;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.lang3.StringUtils;

public abstract class utils {

    static Handler handler;
    static Logger logger;
    static List<String> filesListInDir = new ArrayList<String>();

    public static void error(Patient patient, String err) throws IOException {

        if (err.equals(Constants.ERROR_PATIENT_NO_NIDA)) {
            log("[Warning] The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field Nida  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_EMPTY)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " is empty  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_PCID)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field Prima care ID  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_BIRTHDATE)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field birth date  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_NAME)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field name ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_GENDER)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field gender  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_FATHERNAME)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field father name  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_MOTHERNAME)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field mother name  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_ADDRESS)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field address  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_COUNTRY)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field country ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_PROVINCE)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field province  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_DISTRICT)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field district  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_SECTOR)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field sector ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_CELL)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field cell  ");
        }

        if (err.equals(Constants.ERROR_PATIENT_NO_UMUDUGUDU)) {
            log("The patient " + patient.getId() + " with Fosa ID " + patient.getExtensionByUrl("fosaId").getValue()
                    + " lacks mandatory field umudugudu  ");
        }

        if (!err.equals(Constants.ERROR_PATIENT_NO_NIDA)) {
            error(err);
        }

    }

    public static void error(String err) {
        OperationOutcome oo = new OperationOutcome();
        CodeableConcept detailCode = new CodeableConcept();
        detailCode.setText(err);
        oo.addIssue().setSeverity(OperationOutcome.IssueSeverity.FATAL).setDetails(detailCode);
        throw new InternalErrorException(err, oo);
    }

    public static void log(String msg) throws IOException {
        boolean append = true;
        LocalDate currentdate = LocalDate.now();
        int currentYear = currentdate.getYear();
        Month currentMonth = currentdate.getMonth();

        String logsHome = System.getenv("CATALINA_HOME");
        if (logsHome == null) {
            logsHome = System.getProperty("user.home");
        }

        File logs = new File(logsHome + "/" + Constants.RHIES_CLIENT_REGISTRY_FOLDER + "/rhiesCRlogs");

        if (!logs.exists()) {
            logs.mkdir();
        }
        File logYear = new File(logs + "/" + +currentYear);
        if (logs.exists()) {
            if (!logYear.exists()) {
                logYear.mkdir();
            }

            String[] logsDirItems = logs.list();

            for (String dirItem : logsDirItems) {
                if (new File(logs.getPath() + "/" + dirItem).isDirectory() && StringUtils.isNumeric(dirItem)) {
                    if (!dirItem.equals(Integer.toString(currentYear))) {
                        File dirToZip = new File("logs/" + dirItem);
                        zip(dirToZip.getAbsolutePath(), dirToZip.getAbsolutePath() + ".zip");
                        deleteDir(dirToZip);
                    }
                }
            }
        }

        File logMonth = new File(logs + "/" + currentYear + "/" + currentMonth);
        if (logs.exists()) {
            if (logYear.exists()) {
                if (!logMonth.exists()) {
                    logMonth.mkdir();
                }
            }
        }

        String logDate = logs + "/" + currentYear + "/" + currentMonth + "/" + currentdate + ".log";
        if (logs.exists()) {
            if (logYear.exists()) {
                if (logMonth.exists()) {

                    handler = new FileHandler(logDate, append);
                    // Creating SimpleFormatter
                    Formatter simpleFormatter = new SimpleFormatter();
                    logger = Logger.getLogger("rhies_PatientResourceProvider.class");
                    // Setting formatter to the handler
                    handler.setFormatter(simpleFormatter);
                    logger.addHandler(handler);
                    logger.warning("Failed: " + msg);

                    // Setting Level to ALL
                    handler.setLevel(Level.ALL);
                    logger.setLevel(Level.ALL);
                    handler.close();
                }
            }
        }
    }

    private static void deleteDir(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        dir.delete();
    }

    public static void unzip(String ZipFile, String destDr, String password) {
        try {
            ZipFile zipFile = new ZipFile(ZipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destDr);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void zip(String folderToZip, String ZipFileName) {
        try {
            ZipFile zipFile = new ZipFile(ZipFileName);
            zipFile.createZipFileFromFolder(folderToZip, new ZipParameters(), false, 0);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     *
     * @param dir
     * @throws IOException
     */
    private static void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                filesListInDir.add(file.getAbsolutePath());
            } else {
                populateFilesList(file);
            }
        }
    }
}
