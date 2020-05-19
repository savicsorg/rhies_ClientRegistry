# RHIES Client Registry

This project is client Registry project created using Hapifhir

### Prerequisites ###
- Oracle Java (JDK) installed: Minimum JDK8 or newer.
- Apache Maven build tool (newest version)


### build and run ###
* Start by cloning the project 
``` git clone https://github.com/savicsorg/fhirstarters.git ``` 

``` cd .\rhies_ClientRegistry\ ```

``` cd .\rhies-client-registry\ ```

* Run the following command to compile the project and start a local testing server that runs it:

```
mvn jetty:run
```

* Point your browser to the following URL:

```
http://localhost:8080/Patient

```

If you need to run this server on a different port (using Maven), you can change the port in the run command as follows:

```bash
mvn -Djetty.port=8888 jetty:run
```

And replacing 8888 with the port of your choice.

