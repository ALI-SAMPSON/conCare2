package io.icode.concaregh.app.sms;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.icode.concaregh.app.models.Orders;

/**
     * An Example Class to use for the submission using HTTP API You can perform
     * your own validations into this Class For username, password,destination,
     * source, dlr, type, message, server and port
     **/
    public class Sender {

        // object of the Payment
        private Orders orders;

        // Object of the DatabaseReference to the payment classs
        private DatabaseReference ordersRef;

        // Username that is to be used for submission
        private String username;
        // password that is to be used along with username

        private String password;
        // Message content that is to be transmitted

        private String message;
        /**
         * What type of the message that is to be sent
         * <ul>
         * <li>0:means plain text</li>
         * <li>1:means flash</li>
         * <li>2:means Unicode (Message content should be in Hex)</li>
         * <li>6:means Unicode Flash (Message content should be in Hex)</li>
         * </ul>
         */
        private String type;
        /**
         * Require DLR or not
         * <ul>
         * <li>0:means DLR is not Required</li>
         * <li>1:means DLR is Required</li>
         * </ul>
         */
        private String dlr;
        /**
         * Destinations to which message is to be sent For submitting more than one

         * destination at once destinations should be comma separated Like
         * 91999000123,91999000124
         */
        private String destination;

        // Sender Id to be used for submitting the message
        private String source;

        // To what server you need to connect to for submission
        private String server = "rslr.connectbind.com";

        // Port that is to be used like 8080 or 8000
        private int port = 2345;

        public Sender(String server,
                      int port,
                      String username,
                      String password,
                      String message,
                      String dlr,
                      String type,
                      String destination,
                      String source) {
            this.username = username;
            this.password = password;
            this.message = message;
            this.dlr = dlr;
            this.type = type;
            this.destination = destination;
            this.source = source;
            this.server = server;
            this.port = port;

            // instantiation of the class
            orders = new Orders();

            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        }
        public void submitMessage(){

            try {
                // Url that will be called to submit the message
                URL sendUrl = new URL("http://" + this.server + ":" + this.port
                        + "/bulksms/bulksms");
                HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
                        .openConnection();
                // This method sets the method type to POST so that
                // will be send as a POST request
                httpConnection.setRequestMethod("POST");
                // This method is set as true wince we intend to send
                // input to the server
                httpConnection.setDoInput(true);
                // This method implies that we intend to receive data from server.
                httpConnection.setDoOutput(true);
                // Implies do not use cached data
                httpConnection.setUseCaches(false);
                // Data that will be sent over the stream to the server.
                DataOutputStream dataStreamToServer = new DataOutputStream( httpConnection.getOutputStream());
                        dataStreamToServer.writeBytes("username="
                        + URLEncoder.encode(this.username, "UTF-8") + "&password="
                        + URLEncoder.encode(this.password, "UTF-8") + "&type="
                        + URLEncoder.encode(this.type, "UTF-8") + "&dlr="
                        + URLEncoder.encode(this.dlr, "UTF-8") + "&destination="
                        + URLEncoder.encode(this.destination, "UTF-8") + "&source="
                        + URLEncoder.encode(this.source, "UTF-8") + "&message="
                        + URLEncoder.encode(this.message, "UTF-8"));
                        dataStreamToServer.flush();
                        dataStreamToServer.close();
                // Here take the output value of the server.
                BufferedReader dataStreamFromUrl = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()));
                String dataFromUrl = "", dataBuffer = "";
// Writing information from the stream to the buffer
                while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
                    dataFromUrl += dataBuffer;
                }
/**
 * Now dataFromUrl variable contains the Response received from the
 * server so we can parse the response and process it accordingly.
 */
                dataStreamFromUrl.close();
                System.out.println("Response: " + dataFromUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /*public static void main(String[] args) {
            try {
                // Below exmaple is for sending Plain text
                Sender s = new Sender("smpp2.routesms.com", 8080, "tester909", "test11", "test for unicode", "1", "0", "919869533416",
                        "Update"); s.submitMessage();
                // Below example is for sending unicode
                Sender s1 = new Sender("smpp2.routesms.com", 8080, "xxxx", "xxx", convertToUnicode("test for unicode").toString(),
                        "1", "2", "919869533416", "Update");
                s1.submitMessage();
            }
            catch (Exception ex) {}
        }*/

        /**
         * Below method converts the unicode to hex value
         * @param regText
         * @return
         */
        public static StringBuffer convertToUnicode(String regText) {
            char[] chars = regText.toCharArray();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < chars.length; i++){
                String iniHexString = Integer.toHexString((int) chars[i]);
                if(iniHexString.length() == 1)
                    iniHexString = "000" + iniHexString;
                else if (iniHexString.length() == 2) iniHexString = "00" + iniHexString;
                else if (iniHexString.length() == 3) iniHexString = "0" + iniHexString;
                     hexString.append(iniHexString);
            }
            System.out.println(hexString);
            return hexString;
        }
    }

