package com.czj.schoolcenter;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.czj.schoolcenter.db.DataBaseDao;
import com.czj.schoolcenter.domain.Grade;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	ImageView iv_verifycode;
	EditText username;
	EditText password;
	EditText verifycode;
	private String TAG = "MainActivity";
	ArrayList<Grade> grades;
	private SharedPreferences sp;
	private DataBaseDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dao = new DataBaseDao(this);
		iv_verifycode = (ImageView) findViewById(R.id.iv_verifycode);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		verifycode = (EditText) findViewById(R.id.verifycode);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		grades = new ArrayList<Grade>();

		getCookies();

	}

	private void parseHtml(String html) {
		Document document = Jsoup.parse(html);
		Elements trs = document.select("tr");
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");

			String result = tds.text();
			System.out.println("��" + i + "����" + result);
			if (i > 1) {
				String[] ss = result.split(" ");
				ContentValues value = new ContentValues();
				value.put("id", ss[0]);
				value.put("term", ss[1]);
				value.put("number", ss[2]);
				value.put("name", ss[3]);
				value.put("score", ss[4]);
				value.put("credit", ss[5]);
				dao.insert(value);

			}

		}
	}

	private static int parseInt(String index) {
		int translate = Integer.parseInt(index);
		return translate;
	}

	private String encrypt(String encrypt) {
		if (encrypt == null) {

			Log.i(TAG, "У����Ϊ�գ�");
			return null;
		}
		String name="1341904208";
		String pass="che19940624";
//		String name = username.getText().toString().trim();
//		String pass = password.getText().toString().trim();

		String code = name + "%%%" + pass;
		String scode = encrypt.split("#")[0];

		Log.i(TAG, "�ָ�ǰ��Ϊ" + scode);
		String sxh = encrypt.split("#")[1];

		Log.i(TAG, "�ָ���Ϊ" + sxh);
		String encoded = "";
		for (int i = 0; i < code.length(); i++) {
			if (i < 20) {
				encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
				scode = scode.substring(parseInt(sxh.substring(i, i + 1)), scode.length());
			} else {
				encoded = encoded + code.substring(i, code.length());
				i = code.length();
			}
		}

		return encoded;

	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	/**
	 * ��һ�����󣬵õ���ʼ������cookie
	 */
	private void getCookies() {

		new Thread() {

			@SuppressWarnings("deprecation")
			public void run() {
				String url = "http://jwgl.just.edu.cn:8080/";
				@SuppressWarnings("deprecation")
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				try {
					// get.addHeader("Host", "jwgl.just.edu.cn:8080");
					//
					// get.addHeader("Accept",
					// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					// get.addHeader("Accept-Language",
					// "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
					// get.addHeader("Accept-Encoding", "gzip, deflate");
					// get.addHeader("Connection", "keep-alive");
					HttpResponse response = httpClient.execute(get);
					HttpEntity entity = response.getEntity();
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (entity != null) {
						entity.consumeContent();
					}
					if (cookies.isEmpty()) {
						Log.i(TAG, "��һ������cookieΪ��");
					} else {
						for (int i = 0; i < cookies.size(); i++) {
							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							String path = cookies.get(i).getPath();
							Editor editor = sp.edit();
							editor.putString("cookie" + i + "name", name);
							editor.putString("cookie" + i + "value", value);
							editor.commit();
						}
					}
					httpClient.getConnectionManager().shutdown();

					getverifycode();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					get.abort();
				}
			};
		}.start();

	}

	/*
	 * �ڶ������󣬵õ���֤��͵�����cookie
	 */
	public void getverifycode() {

		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				String cookiesend = sp.getString("cookie0name", "") + "=" + sp.getString("cookie0value", "") + "; "
						+ sp.getString("cookie1name", "") + "=" + sp.getString("cookie1value", "");

				Log.i(TAG, "�ڶ��������cookie�ַ���Ϊ��" + cookiesend);
				String requesturl = "http://jwgl.just.edu.cn:8080/verifycode.servlet";
				DefaultHttpClient httpClient = new DefaultHttpClient();
				@SuppressWarnings("deprecation")
				HttpGet get = new HttpGet(requesturl);
				try {
					get.setHeader("Cookie", cookiesend);
					// get.addHeader("Connection", "keep-alive");
					// get.addHeader("Referer",
					// "http://jwgl.just.edu.cn:8080/");
					// get.addHeader("Accept-Encoding", "gzip, deflate");
					// get.addHeader("Accept-Language",
					// "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
					// get.addHeader("Accept",
					// "image/png,image/*;q=0.8,*/*;q=0.5");
					//
					// get.addHeader("Host", "jwgl.just.edu.cn:8080");
					HttpResponse response = httpClient.execute(get);
					HttpEntity entity = response.getEntity();
					int code = response.getStatusLine().getStatusCode();
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (entity != null && code < 400) {
						InputStream inputStream = entity.getContent();
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

						runOnUiThread(new Runnable() {
							public void run() {
								if (bitmap != null) {

									iv_verifycode.setImageBitmap(bitmap);
								} else {

								}
							}
						});
					}
					if (cookies.isEmpty()) {

						Log.i(TAG, "�ڶ�������cookieΪ��");
					} else {
						Log.i(TAG, "�ڶ�������cookie��Ϊ��");
						for (int i = 0; i < cookies.size(); i++) {
							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							String path = cookies.get(i).getPath();
							Editor editor = sp.edit();
							editor.putString("cookie" + i + "name", name);
							editor.putString("cookie" + i + "value", value);
							editor.commit();
						}
					}
					httpClient.getConnectionManager().shutdown();

					Log.i(TAG, "�ڶ����������");
					getencryptcode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					get.abort();
				}

			};

		}.start();
	}

	String encrypt;

	/**
	 * ����������
	 */
	public void getencryptcode() {
		try {
			Thread.sleep(5000);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.i(TAG, "����������ʼ");
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				String cookiesend = sp.getString("cookie0name", "") + "=" + sp.getString("cookie0value", "") + "; "
						+ sp.getString("cookie1name", "") + "=" + sp.getString("cookie1value", "");

				Log.i(TAG, "�����������cookie�ַ���Ϊ��" + cookiesend);
				String requesturl = "http://jwgl.just.edu.cn:8080/Logon.do?method=logon&flag=sess";
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost(requesturl);
				try {
					post.setHeader("Cookie", cookiesend);
					HttpResponse response = httpClient.execute(post);
					HttpEntity entity = response.getEntity();

					int code = response.getStatusLine().getStatusCode();
					Log.i(TAG, "�����λ����Ӧ��Ϊ" + code);
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (entity != null && code < 400) {
						encrypt = EntityUtils.toString(entity);
						Log.i(TAG, "������Ϊ" + encrypt);
					}
					if (cookies.isEmpty()) {
						Log.i(TAG, "����������cookieΪ��");
					} else {

						Log.i(TAG, "����������cookie��Ϊ��");
						for (int i = 0; i < cookies.size(); i++) {

							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							String path = cookies.get(i).getPath();
							System.out.println("����������õ�cookie��" + name + ";" + value + ";" + path);
						}
					}
					httpClient.getConnectionManager().shutdown();

					Log.i(TAG, "�������������");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					post.abort();
				}
			};
		}.start();

	}

	public void Login(View view) {
		getLocation();
	}

	String locUrl;

	/**
	 * ���Ĵ�����
	 */
	public void getLocation() {

		Log.i(TAG, "���Ĵ�����ʼ");
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				String verify = verifycode.getText().toString().trim();
				String encryptcode = encrypt(encrypt);
				Log.i(TAG, "���ܺ���ַ���Ϊ��" + encryptcode);
				String cookiesend = sp.getString("cookie0name", "") + "=" + sp.getString("cookie0value", "") + "; "
						+ sp.getString("cookie1name", "") + "=" + sp.getString("cookie1value", "");
				Log.i(TAG, "���Ĵ������cookie�ַ���Ϊ��" + cookiesend);
				String requesturl = "http://jwgl.just.edu.cn:8080/Logon.do?method=logon";
				DefaultHttpClient httpClient = new DefaultHttpClient();

				httpClient.setRedirectHandler(new RedirectHandler() {

					@Override
					public boolean isRedirectRequested(HttpResponse arg0, HttpContext arg1) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public URI getLocationURI(HttpResponse arg0, HttpContext arg1) throws ProtocolException {
						// TODO Auto-generated method stub
						return null;
					}
				});
				HttpPost post = new HttpPost(requesturl);
				try {
					post.setHeader("Cookie", cookiesend);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("useDogCode", ""));
					params.add(new BasicNameValuePair("encoded", encryptcode));
					params.add(new BasicNameValuePair("RANDOMCODE", verify));
					Log.i(TAG, "��������ͷ");
					// ����UrlEncodedFormEntity����
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					Log.i(TAG, "����ʵ��");
					HttpResponse response = httpClient.execute(post);
					Log.i(TAG, "ִ����excute����");
					Header location = response.getFirstHeader("Location");
					if (location != null) {
						locUrl = location.getValue();
						Log.i(TAG, "�õ��ض�����ַ" + locUrl);
					}
					int code = response.getStatusLine().getStatusCode();
					Log.i(TAG, "���Ĵ���Ӧ��" + code);
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (cookies.isEmpty()) {
						Log.i(TAG, "���Ĵη���cookieΪ��");
					} else {
						for (int i = 0; i < cookies.size(); i++) {
							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							String path = cookies.get(i).getPath();
							Editor editor = sp.edit();
							editor.putString("cookie" + i + "name", name);
							editor.putString("cookie" + i + "value", value);
							editor.commit();
						}
					}
					httpClient.getConnectionManager().shutdown();
					Log.i(TAG, "���Ĵ��������");
					getThirdCookie();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					post.abort();
				}
			};
		}.start();
	}

	public String divideUrl(String url) {
		String part1 = url.split(" ")[0];
		System.out.println("part1:" + part1);
		String part2 = url.split(" ")[1];
		System.out.println("part2:" + part2);

		String newurl = part1 + "%20" + part2;
		System.out.println("new url" + newurl);
		return newurl;
	}

	private void getThirdCookie() {
		try {
			Thread.sleep(5000);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				String cookiesend = sp.getString("cookie0name", "") + "=" + sp.getString("cookie0value", "") + "; "
						+ sp.getString("cookie1name", "") + "=" + sp.getString("cookie1value", "");
				Log.i(TAG, "����������cookie�ַ���Ϊ��" + cookiesend);
				String requesturl = divideUrl(locUrl);
				DefaultHttpClient httpClient = new DefaultHttpClient();
				@SuppressWarnings("deprecation")
				HttpGet get = new HttpGet(requesturl);
				try {
					get.setHeader("Cookie", cookiesend);
					HttpResponse response = httpClient.execute(get);
					HttpEntity entity = response.getEntity();
					int code = response.getStatusLine().getStatusCode();
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (cookies.isEmpty()) {
						Log.i(TAG, "���������cookieΪ��");
					} else {

						for (int i = 0; i < cookies.size(); i++) {
							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							Log.i(TAG, "�õ����������cookie" + name + "" + value);
							Editor editor = sp.edit();
							editor.putString("cookie3" + "name", name);
							editor.putString("cookie3" + "value", value);
							editor.commit();
						}
					}
					httpClient.getConnectionManager().shutdown();
					Log.i(TAG, "������������");
					lastrequest();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					get.abort();
				}
			};
		}.start();
	}

	String mainpage;

	private void lastrequest() {
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				String cookiesend = sp.getString("cookie3name", "") + "=" + sp.getString("cookie3value", "") + "; "
						+ sp.getString("cookie0name", "") + "=" + sp.getString("cookie0value", "") + "; "
						+ sp.getString("cookie1name", "") + "=" + sp.getString("cookie1value", "");

				Log.i(TAG, "�����������cookie�ַ���Ϊ��" + cookiesend);
				String requesturl = "http://jwgl.just.edu.cn:8080/jsxsd/kscj/cjcx_list";
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost(requesturl);
				try {
					post.setHeader("Cookie", cookiesend);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("kksj", "2013-2014-1"));
					params.add(new BasicNameValuePair("kcxz", ""));
					params.add(new BasicNameValuePair("kcmc", ""));
					params.add(new BasicNameValuePair("xsfs", "all"));
					Log.i(TAG, "��������ͷ");
					// ����UrlEncodedFormEntity����
					post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					Log.i(TAG, "����ʵ��");
					HttpResponse response = httpClient.execute(post);
					HttpEntity entity = response.getEntity();

					int code = response.getStatusLine().getStatusCode();
					Log.i(TAG, "�����λ����Ӧ��Ϊ" + code);
					List<Cookie> cookies = httpClient.getCookieStore().getCookies();
					if (entity != null && code < 400) {
						mainpage = EntityUtils.toString(entity);
						parseHtml(mainpage);
						Editor editor = sp.edit();
						editor.putString("mainpage", mainpage);
						editor.commit();
						Log.i(TAG, "������Ϊ" + mainpage);
					}
					if (cookies.isEmpty()) {
						Log.i(TAG, "����������cookieΪ��");
					} else {

						Log.i(TAG, "����������cookie��Ϊ��");
						for (int i = 0; i < cookies.size(); i++) {

							String name = cookies.get(i).getName();
							String value = cookies.get(i).getValue();
							String path = cookies.get(i).getPath();
							System.out.println("����������õ�cookie��" + name + ";" + value + ";" + path);
						}
					}
					httpClient.getConnectionManager().shutdown();

					Log.i(TAG, "�������������");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					post.abort();
				}
			};
		}.start();
		startActivity(new Intent(MainActivity.this, GradeActivity.class));
	}
}