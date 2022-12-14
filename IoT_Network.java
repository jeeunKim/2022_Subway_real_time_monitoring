package IoT_Project;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class IoT_Project{
	static MqttClient sampleClient;
		
    public static void main(String[] args) {
    	connectBroker();
    	
    	String[] subway_data  = get_subway_data();
    	
    	publish_data("statnNm", "{\"statnNm\": "+ '"'+subway_data[0]+'"'+"}");
    	publish_data("recptnDt", "{\"recptnDt\": "+ '"'+subway_data[1]+'"'+"}");
    	publish_data("updnLine", "{\"updnLine\": "+'"'+subway_data[2]+'"'+"}");
    	publish_data("statnTnm", "{\"statnTnm\": "+'"'+subway_data[3]+'"'+"}");
    	publish_data("trainSttus", "{\"trainSttus\": "+'"'+subway_data[4]+'"'+"}");
    	publish_data("lstcarAt", "{\"lstcarAt\": "+'"'+subway_data[5]+'"'+"}");
    	
    	
    	try {
			sampleClient.disconnect();
	        System.out.println("Disconnected");
	        System.exit(0);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void connectBroker() {
        String broker       = "tcp://127.0.0.1:1883";
        String clientId     = "Project";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public static void publish_data(String topic_input, String data) {
        String topic        = topic_input;
        int qos             = 0;
        try {
            System.out.println("Publishing message: "+data);
            sampleClient.publish(topic, data.getBytes(), qos, false);
            System.out.println("Message published");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public static String[] get_subway_data() {
    	Date current = new Date(System.currentTimeMillis());
    	SimpleDateFormat d_format = new SimpleDateFormat("yyyyMMddHHmmss"); 
    	//System.out.println(d_format.format(current));
    	String date = d_format.format(current).substring(0,8);
    	String time = d_format.format(current).substring(8,10);
    	String url = "http://swopenapi.seoul.go.kr/api/subway/sample/xml/realtimePosition/1/1/2%ED%98%B8%EC%84%A0";
    	
		String statnNm = "";         //???????????????
		String recptnDt = "";    	 //??????????????????
		String updnLine = "";        //?????????????????? (2??????(1:?????? 0:??????), 0:?????? 1:??????)
		String statnTnm = "";        //?????????????????????
		String trainSttus = "";      //?????????????????? (0:?????? 1:??????, 0,1??? ????????????:??????)
		String lstcarAt = "";        //????????????(1:??????, 0:??????)
				
    	Document doc = null;
		
		// 2.??????
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("row");
		for (Element e : elements) {
			if (e.select("subwayNm").text().equals("2??????")) {
				statnNm = e.select("statnNm").text();
				recptnDt = e.select("recptnDt").text();
				updnLine = e.select("updnLine").text();
				statnTnm = e.select("statnTnm").text();
				trainSttus = e.select("trainSttus").text();
				lstcarAt = e.select("lstcarAt").text();
			}
		}
		String[] out = {statnNm,recptnDt,updnLine,statnTnm,trainSttus,lstcarAt};
    	return out;
    } 
}
