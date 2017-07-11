package cn.lsy.lsynote.util;

public interface NoteApi {
	String METHOD_POST = "POST"; // 取值必须是大写的
	String METHOD_GET = "GET"; // 取值必须是大写的
	
	String URL_HOST = "http://172.26.146.222:8080/note/";
	

	
	String URL_LOGIN = URL_HOST + "user/login.do";
	String URL_REGISTER = URL_HOST + "user/regist.do";
	String URL_DIRECTORY_ADD = URL_HOST + "notebook/add.do";
	String URL_DIRECTORY_LIST = URL_HOST + "notebook/list.do";
	String URL_NOTE_LIST = URL_HOST + "note/list.do";
	
	String PARAM_USER_ID = "userId";
	String PARAM_NOTEBOOK_ID = "notebookId";
	String PARAM_TITLE = "title";
	String PARAM_DIRECTORY_NAME = "name";
	String PARAM_USERNAME = "name";
	String PARAM_NICKNAME = "nick";
	String PARAM_PASSWORD = "password";
	String PARAM_PASSWORD_COMFIRM = "confirm";
	

}
