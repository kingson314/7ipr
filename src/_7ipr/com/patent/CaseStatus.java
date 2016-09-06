package _7ipr.com.patent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @Description:
 * @date 2015-07-07
 * @author:fgq
 */
public class CaseStatus{
	private  static String url = "http://cpquery.sipo.gov.cn/txnQueryBibliographicData.do?select-key:shenqingh=";
	private  static String[] StatusList = new String[] {
		"待分配","新案审查","回案审查","等待提案","等待公布",
		"公布封卷","公布完成","准备公布","进入实审","实审检查",
		"公告封卷","驳回失效","分案结案","国优视撤","等待申请费",
		"初审待答复","审查待提取","等待提实审","待质检抽案","等年登印费",
		"专利权维持","等待实审请求","等待案件分配","中间文件审查","退件(审查)",
		"期限费用审查","准备等待公布","等待实审提案","等待裁决转案","一通回案实审",
		"中通回案实审","一通回案形审","中通回案形审","等待颁证公告","准备颁证公告",
		"等年费滞纳金","视为放弃失效","逾期视撤失效","届满终止失效","无效宣告失效",
		"撤回专利申请","驳回等复审请求","待退件(审查)","一通出案待答复","中通出案待答复",
		"形审补正待答复","逾期视撤,等恢复","准备进入外观分类","准备进入发明初审","准备进入新型初审",
		"准备进入实审阶段","视为放弃,等恢复","届满终止,待失效","无申请费视撤失效","实审请求视撤失效",
		"未缴年费终止失效","其他通知书回案实审","其他通知书回案形审","待发授权办登通知书","逾期视撤,等恢复补正",
		"实审请求视撤,等恢复","其他通知书出案待答复","视为放弃,等恢复补正","未缴申请费视撤,等恢复","放弃专利权（重复授权）",
		"放弃专利权（主动放弃）","实审请求视撤,等恢复补正","未缴年费终止,等恢复补正","未缴申请费视撤,等恢复补正","未缴年费专利权终止,等恢复",
		"放弃专利权（同样发明创造）","逾期视撤,等恢复（发明初审）","逾期视撤,等恢复补正（发明初审）"};
	
	private static String[] validStatus=new String[]{"专利权维持","公告封卷","等年费滞纳金","等待颁证公告","准备颁证公告"};
	private static String[] invalidStatus=new String[]{"视为放弃失效","逾期视撤失效","届满终止失效","无效宣告失效","撤回专利申请",
			"驳回失效","分案结案","国优视撤","届满终止,待失效","无申请费视撤失效",
			"实审请求视撤失效","未缴年费终止失效","放弃专利权（重复授权）","放弃专利权（主动放弃）","放弃专利权（同样发明创造）"};
	private static String[] auditingStatus=new String[]{"待分配","新案审查","回案审查","等待提案","等待公布",
			"公布封卷","公布完成","准备公布","进入实审","实审检查",
			"等待申请费","初审待答复","审查待提取","等待提实审","待质检抽案",
			"等待实审请求","等待案件分配","中间文件审查","退件(审查)","期限费用审查",
			"准备等待公布","等待实审提案","等待裁决转案","一通回案实审","中通回案实审",
			"一通回案形审","中通回案形审","逾期视撤,等恢复","逾期视撤,等恢复补正","实审请求视撤,等恢复",
			"未缴申请费视撤,等恢复","待退件(审查)","一通出案待答复","中通出案待答复","形审补正待答复",
			"驳回等复审请求","准备进入外观分类","准备进入发明初审","准备进入新型初审","准备进入实审阶段",
			"实审请求视撤,等恢复补正","未缴年费终止,等恢复补正","未缴申请费视撤,等恢复补正","未缴年费专利权终止,等恢复","逾期视撤,等恢复（发明初审）",
			"逾期视撤,等恢复补正（发明初审）","其他通知书回案实审","其他通知书回案形审","待发授权办登通知书","其他通知书出案待答复",
			"等年登印费","视为放弃,等恢复","视为放弃,等恢复补正"
	};
	
	private static int tryTimes = 5;
	
	private static CaseStatus instance=null;
	
	public static CaseStatus getInstance(){
		if(instance==null){
			instance=new CaseStatus();
		}
		return instance;
	}

	
	/**
	 * 解析出当前申请号的案件状态，搜索次数，搜索时间
	 * @param appNo
	 * @return
	 */
	private  Map<String, String> getPatentRusult(String appNo) {
		long now = System.currentTimeMillis();
		Map<String, String> map = new HashMap<String, String>();
		String status = "";
		int curTime = 0;
		try {
			String surl = url + appNo;
			//System.out.println(surl);
			Elements elements = null;
			while (curTime < tryTimes) {
				elements = getElements(surl);
				if (elements.size() >= 3) {
					break;
				}
				curTime++;
			}
//			System.out.println("----------------------------");
//			for(Element el:elements){
//				System.out.println(el.html());
//			}
//			System.out.println("----------------------------");
//			System.out.println("       "+curTime);
			// n次之后还是没查出来
			if (elements.size() < 3) {
				status = "STATUS NOT FOUND";
				map.put("success", "false");
				map.put("status", status);
				map.put("time", String
						.valueOf((System.currentTimeMillis() - now)));
				map.put("times", String.valueOf(curTime+1));
				return map;
			}else{
				status = elements.get(2).text();
				status = status.substring(6);
				status = removerepeatedchar(status);
				status = getBySatusName(getStausName(status));

				map.put("success", "true");
				map.put("status", status);
				map.put("time", String.valueOf((System.currentTimeMillis() - now)));
				map.put("times", String.valueOf(curTime+1));
				return map;
			}
		} catch (Exception e) {
			status = "请输入正确的申请号";
			map.put("success", "false");
			map.put("status", status);
			map.put("time", String.valueOf((System.currentTimeMillis() - now)));
			map.put("times", String.valueOf(curTime+1));
			return map;
		}
	}
	/**
	 * 获取当前URL的文档内容
	 * @param surl
	 * @return
	 * @throws Exception
	 */
	private  Elements getElements(String surl) throws Exception {
		Document doc = Jsoup
				.connect(surl)
				.timeout(20000)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
				.ignoreContentType(true).get();
		return doc.select(".imfor_table_grid tr");
	}
	/**
	 * 字符去重
	 * @param str
	 * @return
	 */
	public  String removerepeatedchar(String str) {
		if (str == null)
			return str;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		String rs = "";
		for (String item : list) {
			rs += item;
		}
		return rs;
	}

	/**
	 * 根据从网站获取的状态混码解析出正确的法律状态
	 * @param status
	 * @return
	 */
	private  String getStausName(String status) {
		String rs = status;
		for (String item : StatusList) {
			boolean isTrue = true;
			for (int i = 0; i < status.length(); i++) {
				if (isTrue) {
					char schar = status.charAt(i);
					if(",".indexOf(schar)>=0||"，".indexOf(schar)>=0){
						continue;
					}
					if (item.indexOf(schar) >= 0) {
						isTrue = true;
					} else {
						isTrue = false;
					}
				} else {
					break;
				}
			}
			if (isTrue) {
				return item;
			}
		}
		return "<未识别状态>" + rs;
	}
	/**
	 * 根据案件状态判断，有效，无效，审中状态
	 * @param status
	 * @return
	 */
	public String getBySatusName(String status){
		for(String sta:validStatus){
			if(sta.equals(status)){
				return "有效";
			}
		}
		for(String sta:invalidStatus){
			if(sta.equals(status)){
				return "无效";
			}
		}
		for(String sta:auditingStatus){
			if(sta.equals(status)){
				return "在审";
			}
		}
		return "";
	}
	/**
	 * 根据申请号获取法律状态
	 * @param appNo
	 * @return
	 */
	public String getByApplicationNo(String appNo) {
		String rs="";
		appNo=appNo.replace("CN", "").replace(".", "").trim();
		Map<String, String> map=getPatentRusult(appNo);
		if("true".equals(map.get("success"))){
			rs= map.get("status");
		}else{
			rs= "";
		}
		return rs;
	} 
	
	public String getByAppNo_Status(String appNo,String status){
		String rs=getByApplicationNo(appNo);
		if("".equals(rs)){
			rs= getBySatusName(status);
		}
		return rs;
	}
	public static void main(String[] args) {
		String appNo="CN201610200916.0";
		System.out.println( CaseStatus.getInstance().getByApplicationNo(appNo));
		String status="新案审查";
		System.out.println( CaseStatus.getInstance().getBySatusName(status));
		
//		appNo="20142005143881";
//		System.out.println( CaseStatus.getInstance().getByAppNo_Status(appNo,status));
	}
}
