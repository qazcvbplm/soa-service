package sunwou.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.google.gson.Gson;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sunwou.exception.MyException;

/**
 * @author onepieces
 */
public class Util {

	private static Logger logger = LoggerFactory.getLogger(Util.class);
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static Gson gson = new Gson();
	// 产品名称:云通信短信API产品,开发者无需替换
	static final String product = "Dysmsapi";
	// 产品域名,开发者无需替换
	static final String domain = "dysmsapi.aliyuncs.com";
	// 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	static final String accessKeyId = "LTAIayU67yoV65lP";
	static final String accessKeySecret = "mVu8ZgETcXzPOBglnovQl1nanDB2Jn";

	public static void outLog(String info) {
		logger.warn(info);
	}

	public static void outerror(String info) {
		logger.error(info);
	}
	

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 */
	 public static String getIpAddr(HttpServletRequest request){  
         String ipAddress = request.getHeader("x-forwarded-for");  
             if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                 ipAddress = request.getHeader("Proxy-Client-IP");  
             }  
             if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                 ipAddress = request.getHeader("WL-Proxy-Client-IP");  
             }  
             if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                 ipAddress = request.getRemoteAddr();  
                 if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                     //根据网卡取本机配置的IP  
                     InetAddress inet=null;  
                     try {  
                         inet = InetAddress.getLocalHost();  
                     } catch (UnknownHostException e) {  
                         e.printStackTrace();  
                     }  
                     ipAddress= inet.getHostAddress();  
                 }  
             }  
             //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
             if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
                 if(ipAddress.indexOf(",")>0){  
                     ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
                 }  
             }  
             return ipAddress;   
     } 
	/**
	 * 
	 * @param requestUrl请求地址
	 * @param requestMethod请求方法
	 * @param outputStr参数
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
		// 创建SSLContext
		StringBuffer buffer = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(requestMethod);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();

			// 往服务器端写内容
			if (null != outputStr) {
				OutputStream os = conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}
			// 读取服务器端返回的内容
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}




	/**
	 * 根据用户的唯一ID 生成订单编号
	 * 
	 * @param openid
	 * @param perfix
	 * @return
	 */
	public static String GenerateOrderNumber(String openid, String perfix) {
		int random = (int) (Math.random() * (openid.length()));
		return perfix + "" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + random
				+ openid.charAt(random);
	}

	/**
	 * 解析xml格式数据
	 * 
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> parseXML(HttpServletRequest req) throws IOException {
		// 解析结果存储在HashMap
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = req.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(inputStream);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}
		// 释放资源
		inputStream.close();
		inputStream = null;
		return map;
	}

	/**
	 * 设置xml返回数据
	 * 
	 * @param tag
	 *            标签内同
	 * @param content
	 *            数据内容
	 * @return
	 */
	public static String setXML(String[] tag, String[] content) {
		if (tag.length != content.length)
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < tag.length; i++) {
			sb.append("<" + tag[i] + "><![CDATA[" + content[i] + "]]></" + tag[i] + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}


	// 字符串转成二维码base64
	public static String GetImageStr(String content) throws Exception {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		BufferedImage image = QRCodeUtil.createImage(content, null, true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(baos.toByteArray());// 返回Base64编码过的字节数组字符�?
	}

	// base64字符串转化成图片
	public static boolean GenerateImage(String imgStr, String outfile) { // 对字节数组字符串进行Base64解码并生成图�?
		if (imgStr == null) // 图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr.split(",")[1]);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(outfile);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 生成随机颜色
	 *
	 * @return
	 */
	public static String getRandColorCode() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return r + g + b;
	}

	/**
	 * 获取字符串拼音的第一个字母
	 * 
	 * @param chinese
	 *            输入的中文字符串
	 * @return 首字
	 *//*
	public static String ToFirstChar(String chinese) {
		StringBuilder pinyinStr = new StringBuilder();
		char[] newChar = chinese.toCharArray(); // 转为单个字符
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (char aNewChar : newChar) {
			if (aNewChar > 128) {
				try {
					pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(aNewChar, defaultFormat)[0].charAt(0));
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinStr.append(aNewChar);
			}
		}
		return pinyinStr.toString();
	}

	*//**
	 * 将汉字转成拼音
	 *//*
	public static String ToPinyin(String chinese) {
		StringBuilder pinyinStr = new StringBuilder();
		char[] newChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (char aNewChar : newChar) {
			if (aNewChar > 128) {
				try {
					pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(aNewChar, defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinStr.append(aNewChar);
			}
		}
		return pinyinStr.toString();
	}
*/




	public static SendSmsResponse sendSms(String phone,String code) throws ClientException {

		// 可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		// 初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 组装请求对象-具体描述见控制台-文档部分内容
		SendSmsRequest request = new SendSmsRequest();
		// 必填:待发送手机号
		request.setPhoneNumbers(phone);
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName("双沃科技");
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode("SMS_105905098");
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		request.setTemplateParam("{\"code\":\""+code+"\"}");

		// 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
		// request.setSmsUpExtendCode("90997");

		// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		// request.setOutId("yourOutId");

		// hint 此处可能会抛出异常，注意catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

		return sendSmsResponse;
	}
	
	/**
	 * 腾讯发送短信
	 * @param appid
	 * @param appkey
	 * @param phoneNumber
	 * @param templateId
	 * @param params
	 * @throws JSONException
	 * @throws HTTPException
	 * @throws IOException
	 */
	public static void qqsms(int appid,String appkey,String phoneNumber,int templateId,String params,String sign) throws JSONException, HTTPException, IOException{
		  SmsSingleSender sender=new SmsSingleSender(appid, appkey);
		  String[] param=new String[1];
		  param[0]=params;
		  SmsSingleSenderResult result=sender.sendWithParam("86", phoneNumber, templateId, param,sign, "", "");
		  if(result.result!=0){
			  throw new MyException(result.errMsg);
		  }
	}
	

	/**
	 * 对字符串加密
	 * @param content
	 * @return
	 */
	public static String EnCode(String content) {
		String miwen1 = "o" + new String(Base64.getEncoder().encode(content.getBytes())) + "p";
		return new String(Base64.getEncoder().encode(miwen1.getBytes()));
	}

	/**
	 * 对字符串解密
	 * 
	 * @param content
	 * @return
	 */
	public static String DeCode(String content) {
		String mingwen1 = new String(Base64.getDecoder().decode(content));
		return new String(Base64.getDecoder().decode(mingwen1.substring(1, mingwen1.length() - 1)));
	}
	/**
	 * 对参数进行验证
	 */
    public static void checkParams(BindingResult result){
    	    if(result.hasErrors())
    	    {
    	    	throw new MyException(result.getAllErrors().get(0).getDefaultMessage());
    	    }
    }
	
    
   
	 
}
