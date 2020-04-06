/**
 * 
 */
package es.santander.smartcity.ArcgisRestUtils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import es.santander.smartcity.exceptions.ArcgisException;
import es.santander.smartcity.model.Credential;
import es.santander.smartcity.model.Feature;
import es.santander.smartcity.model.UserCredential;

/**
 * @author dmartinez
 *
 */
public class RestFeatureTableTest implements ArcgisBaseTest {

    static final Logger logger = Logger.getLogger(RestFeatureTableTest.class);

    @Before
    public void setUp() {
        BasicConfigurator.configure();
    }

    @Test
    public void ConnectionTest() throws MalformedURLException {
        String serviceUrl = ArcgisBaseTest.getFeatureUrl();
        String tokenUrl = ArcgisBaseTest.getGenerateTokenUrl();

        System.out.println("----------------  ConnectionTest. Portal:" + ArcgisBaseTest.testPortal);

        try {
            Credential credential = new UserCredential(ArcgisBaseTest.getUser(), ArcgisBaseTest.getPassword());
            RestFeatureTable featureTable = new RestFeatureTable(serviceUrl, credential, tokenUrl);

            System.out.println("Connecting....");

            String whereClause = "OBJECTID>0";
            featureTable.getFeatureList(whereClause);

            System.out.println("Token: " + credential.getToken());

            assertTrue("Ejecución correcta", true);

        } catch (ArcgisException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    private RestFeatureTable createFeatureTable(String serviceUrl) throws ArcgisException {
        String tokenUrl = ArcgisBaseTest.getGenerateTokenUrl();

        System.out.println("----------------  PortalConnectionTest");
        Credential credential = new UserCredential(ArcgisBaseTest.getUser(), ArcgisBaseTest.getPassword());
        return new RestFeatureTable(serviceUrl, credential, tokenUrl);
    }

    @Test
    public void ConnectionWithoutCredentialsTest() throws MalformedURLException {
        String serviceUrl = ArcgisBaseTest.getFeatureUrl();
        System.out.println("----------------  ConnectionWithoutCredentialsTest");

        try {
            RestFeatureTable featureTable = createFeatureTable(serviceUrl);

            System.out.println("Connecting....");

            String whereClause = "OBJECTID>0";
            featureTable.getFeatureList(whereClause);

        } catch (ArcgisException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertTrue("Ejecución correcta", true);
    }

    @Test
    public void checkResponse() throws MalformedURLException {
        String responseError = "{\"error\":{\"code\":500,\"message\":\"Unable to complete operation.\",\"details\":[\"Parser error: Some parameters could not be recognized.\"]}}";
        String responseInvalid = "<html></html>";
        String responseOk = "{}";

        try {
            System.out.println("----------------  checkResponse");

            try {
                RestApi.checkHttpResponse(responseError);
                fail("FAILED - Failed detecting error response");
            } catch (ArcgisException e) {
                System.out.println("SUCCESS - Error response successfully detected: " + e.getMessage());
            }

            try {
                RestApi.checkHttpResponse(responseInvalid);
                fail("FAILED - Failed detecting error response");
            } catch (ArcgisException e) {
                System.out.println("SUCCESS - Invalid response successfully detected: " + e.getMessage());
            }

            try {
                RestApi.checkHttpResponse(responseOk);
                System.out.println("SUCCESS - Correct response successfully detected.");
            } catch (ArcgisException e) {
                fail("FAILED - Failed detecting correct response: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.print("FAILED: " + e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    public void addFeature() throws MalformedURLException, ArcgisException {

        System.out.println("----------------  addFeature");
        String serviceUrl = ArcgisBaseTest.getFeatureUrl();
        RestFeatureTable featureTable = createFeatureTable(serviceUrl);

        Feature feature = FeatureTestFactory.getNewOcupacionFeature("Prueba addPortalFeature");
        System.out.println(feature.toJson().toString());

        featureTable.addFeature(feature);

    }

    @Test
    public void updateFeature() throws MalformedURLException, ArcgisException {

        System.out.println("----------------  updateFeature");
        String serviceUrl = ArcgisBaseTest.getFeatureUrl();
        RestFeatureTable featureTable = createFeatureTable(serviceUrl);

        Feature feature = FeatureTestFactory.getUpdatedOcupacionFeature(621, "prueba updatePortalFeature");
        System.out.println(feature.toJson().toString());

        featureTable.updateFeature(feature);

    }

    @Test
    public void deleteFeature() throws MalformedURLException, ArcgisException {
        try {
            System.out.println("----------------  deleteFeature");
            String serviceUrl = ArcgisBaseTest.getFeatureUrl();
            RestFeatureTable featureTable = createFeatureTable(serviceUrl);

            List<String> idList = new ArrayList<String>();
            idList.add("327");
            idList.add("326");

            featureTable.deleteEntities(idList);
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void getFeatures() throws MalformedURLException, ArcgisException {
        try {
            System.out.println("----------------  getOnlineFeatures");
            String serviceUrl = ArcgisBaseTest.getFeatureUrl();
            String tokenUrl = ArcgisBaseTest.getGenerateTokenUrl();
            Credential credential = new UserCredential(ArcgisBaseTest.getUser(), ArcgisBaseTest.getPassword());
            RestFeatureTable featureTable = new RestFeatureTable(serviceUrl, credential, tokenUrl);

            String whereClause = "OBJECTID>0";

            List<Feature> featureList = featureTable.getFeatureList(whereClause);
            assertTrue(featureList.size() > 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void getTableAttributesInfo_noCred() throws MalformedURLException, ArcgisException {
        try {
            System.out.println("----------------  getTableAttributesInfo_noCred");
            String serviceUrl = ArcgisBaseTest.getFeatureUrl();
            RestFeatureTable featureTable = createFeatureTable(serviceUrl);

            featureTable.getTableAttributesInfo();
            System.out.println("Number of fields detected: " + featureTable.getTableAttributes().size());
            System.out.println("Number of Unique fields: " + featureTable.getUniqueAttributes().size());
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void getTableAttributesInfo() throws MalformedURLException, ArcgisException {
        try {
            System.out.println("----------------  getTableAttributesInfo");
            String serviceUrl = "https://sags1/arcgis/rest/services/Policia/SenalesTrafico_ETRS89/MapServer/5";
            serviceUrl = "https://services5.arcgis.com/398f12mJiCbJeoAQ/arcgis/rest/services/OcupacionDummy/FeatureServer/0";
            Credential credential = new UserCredential(ArcgisBaseTest.getUser(), ArcgisBaseTest.getPassword());

            RestFeatureTable featureTable = new RestFeatureTable(serviceUrl, credential,
                    ArcgisBaseTest.getGenerateTokenUrl());

            featureTable.getTableAttributesInfo();
            System.out.println("Number of fields detected: " + featureTable.getTableAttributes().size());
            System.out.println("Number of Unique fields: " + featureTable.getUniqueAttributes().size());
            assertTrue(true);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
