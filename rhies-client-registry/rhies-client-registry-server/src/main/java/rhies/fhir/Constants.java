/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rhies.fhir;

public abstract class Constants {
    public static final String RHIES_CLIENT_REGISTRY_FOLDER = "RhiesClientRegistry";
    public static final String PROPERTIES_FILE_NAME = "RhiesCRConfig.properties";
    public static final String DEFAULT_DB_URL = "localhost";
    public static final String DEFAULT_DB_PORT = "27017";
    public static final String DEFAULT_DB_NAME = "rhiesClientRegistry";

    public static final String ERROR_PATIENT_EMPTY = "Error, can not push empty information to the database!";
    public static final String ERROR_PATIENT_NO_NIDA = "Error, this patient has no nida!";
    public static final String ERROR_PATIENT_NO_PCID = "Error, this patient has no primary care id!";
    public static final String ERROR_PATIENT_NO_NAME = "Error, this patient has no name!";
    public static final String ERROR_PATIENT_NO_BIRTHDATE = "Error, this patient has no birth date!";
    public static final String ERROR_PATIENT_NO_GENDER = "Error, this patient has no gender!";
    public static final String ERROR_PATIENT_NO_FATHERNAME = "Error, this patient has no father name!";
    public static final String ERROR_PATIENT_NO_MOTHERNAME = "Error, this patient has no mother name!";
    public static final String ERROR_PATIENT_NO_ADDRESS = "Error, this patient has no address!";
    public static final String ERROR_PATIENT_NO_COUNTRY = "Error, this patient has no country!";
    public static final String ERROR_PATIENT_NO_PROVINCE = "Error, this patient has no country's province!";
    public static final String ERROR_PATIENT_NO_DISTRICT = "Error, this patient has no province's district!";
    public static final String ERROR_PATIENT_NO_SECTOR = "Error, this patient has no district's sector!";
    public static final String ERROR_PATIENT_NO_CELL = "Error, this patient has no sector's cell!";
    public static final String ERROR_PATIENT_NO_UMUDUGUDU = "Error, this patient has no cell's umudugudu!";

}
