# RHIES Client Registry deployment
Normally this deployment goes along with the global Rhies project deployment.
But if you wish to deploy this particular registry localy:

1. Build and push the RHIES Client Registry on the docker hub (see the read me inside the **ClientRegistry_Docker_Image** directory)
2. Run the **docker-compose up -d** commande within the folder that contains docker-compose.yml file
3. access your client registry at the address: http://localhost:8079/clientregistry/Patient

