package com.bestbuy.android.upgradechecker.activity;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.StoreLocatorList;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.upgradechecker.data.Subscriber;
import com.bestbuy.android.upgradechecker.weblayer.GetNotificationStatusService;
import com.bestbuy.android.upgradechecker.weblayer.PutNotificationService;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.FontLibrary;

public class UpgradeCheckerTermsandConditions extends MenuActivity implements OnClickListener{

	private static final int PROGRESS_DIALOG = 1;
	private static final int GET_STATUS_PROGRESS_DIALOG = 2;
	
	private TextView view_terms_con;
	private String zip;
	private String upgradeEligibilityDateString="";
	private String contractEndDateString=""; 
	private String tradeInValueString=""; 
	private String upgradeEligibilityMessage=""; 
	private String upgradeEligibilityFootnote="";
	private String notificationCode=null;
	private boolean upgradeEligibilityFlag;
	private ImageButton upgrade_chkr_at_t_text;
	private Button textMe,callMe,changeToTextMe,changeToCallMe,findStore;
	private ImageView carrierLogo;
	private String subscriberPhoneNumber;
	private Subscriber subscriber;
	private HashMap results = null;
	private String notificationStatus="";
	private LinearLayout contact_layout;
	private LinearLayout will_call_layout;
	private RelativeLayout textme_and_callme_layout;
	private String intimationText="We'll %s you when you reach your full upgrade date.";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		Intent receivedIntent=getIntent();
        subscriber=(Subscriber) receivedIntent.getSerializableExtra("Subscriber");
        if(subscriber!=null){
        	getSubscriberDetails(subscriber);
        }
        new GetNotificationStatusTask(UpgradeCheckerTermsandConditions.this).execute();
	}
	private void initialiseComponents(){
		
		if(upgradeEligibilityFlag){			
			setContentView(R.layout.upgradechecker_eligible);		 							
		}else{			
			setContentView(R.layout.upgradechecker_noneligibile);
		}
		String upgradeType=subscriber.getUpgradeEligibilityType();
		contact_layout = (LinearLayout) findViewById(R.id.contact_layout);
		TextView upg_txt = (TextView) findViewById(R.id.upg_txt);
		TextView terms_lnik= (TextView) findViewById(R.id.terms_lnik);
		SpannableString spanText=new SpannableString(getResources().getString(R.string.eligibility_date));
		spanText.setSpan(new InternalURLSpan (new OnClickListener(){
			public void onClick(View view) {				
				Intent intent=new Intent(UpgradeCheckerTermsandConditions.this,TermsAndConditionsActivity.class);
				startActivity(intent);
			}
		}),90, 119, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);			
		terms_lnik.setText(spanText);
		terms_lnik.setMovementMethod(LinkMovementMethod.getInstance());
		terms_lnik.setBackgroundColor(Color.TRANSPARENT);
		terms_lnik.setLinksClickable(true);		
		textme_and_callme_layout = (RelativeLayout) findViewById(R.id.textme_and_callme_layout);		
		TextView android_phone_txt= (TextView)findViewById(R.id.android_phone_txt);
		TextView and_receive_txt= (TextView)findViewById(R.id.and_receive_txt);		
		if(upgradeType.equals("ELIGIBLE_FULL")){							
			contact_layout.setVisibility(View.GONE);			
			textme_and_callme_layout.setVisibility(View.GONE);
			upg_txt.setText("UPGRADE YOUR PHONE");							
		}	
		Typeface droidNormal = FontLibrary.getFont(R.string.droidsans, getResources());
		Typeface droidBold = FontLibrary.getFont(R.string.droidsansbold, getResources());
		TextView good_news_text = (TextView)findViewById(R.id.good_news_text);
		TextView goodNewsFootNote= (TextView)findViewById(R.id.good_news_text_meaning);
		if(!upgradeEligibilityFlag)		
			goodNewsFootNote.setVisibility(View.GONE);
		TextView contractDateText= (TextView)findViewById(R.id.at_t_term_contract_detail);
		contractDateText.setTypeface(droidBold);
		TextView fullEligibilityText= (TextView)findViewById(R.id.at_t_term_full_eligibility);
		fullEligibilityText.setTypeface(droidBold);
		TextView phoneNumberText= (TextView)findViewById(R.id.at_t_terms_ph_number);
		phoneNumberText.setTypeface(droidBold);
		
		TextView phoneNumber = (TextView)findViewById(R.id.edit_at_t_term_ph_number);
		phoneNumber.setTypeface(droidNormal);
		
		TextView eligibilityDate = (TextView)findViewById(R.id.edit_at_t_term_eligibility);
		eligibilityDate.setTypeface(droidNormal);
		
		TextView contractEndDate = (TextView)findViewById(R.id.at_t_term_contract_end_date);
		contractEndDate.setTypeface(droidNormal);	
		
		will_call_layout = (LinearLayout) findViewById(R.id.will_call_layout);
		TextView will_call = (TextView) findViewById(R.id.will_call);
		textMe=(Button)findViewById(R.id.upgrade_at_t_text_me);
		callMe=(Button)findViewById(R.id.upgrade_at_t_call_me);
		changeToTextMe=(Button)findViewById(R.id.change_to_text_me);
		changeToCallMe=(Button)findViewById(R.id.change_to_call_me);
		
		if(notificationStatus!=null && notificationStatus.equals("TEXT")  ){
			contact_layout.setVisibility(View.GONE);			
			will_call_layout.setVisibility(View.VISIBLE);			
			textMe.setVisibility(View.GONE);
			callMe.setVisibility(View.GONE);
			changeToCallMe.setVisibility(View.VISIBLE);
			intimationText=String.format(intimationText, "text");
			will_call.setText(intimationText);
		}
		else if( notificationStatus!=null && notificationStatus.equals("VOICE")){
			contact_layout.setVisibility(View.GONE);			
			will_call_layout.setVisibility(View.VISIBLE);
			textMe.setVisibility(View.GONE);
			callMe.setVisibility(View.GONE);
			changeToTextMe.setVisibility(View.VISIBLE);
			intimationText=String.format(intimationText, "call");
			will_call.setText(intimationText);
		}
		carrierLogo=(ImageView)findViewById(R.id.carrier_Logo);
		upgrade_chkr_at_t_text=(ImageButton)findViewById(R.id.upgrade_chkr_at_t_text);
		findStore=(Button)findViewById(R.id.upgrade_at_t_find_a_store);
		if(findStore!=null)
			findStore.setOnClickListener(this);	 
		textMe.setOnClickListener(this);
		callMe.setOnClickListener(this);
		changeToTextMe.setOnClickListener(this);
		changeToCallMe.setOnClickListener(this);		
		if(android_phone_txt!=null)
		{
			String deviceName = subscriber.getTradeInPhoneName();
			if (null == deviceName) {
				deviceName = "current phone";
			}
			android_phone_txt.setText(deviceName+" at your local Best Buy Mobile");
		}
		if(and_receive_txt!=null){
			if (tradeInValueString.equals("")) {
				and_receive_txt.setText("and receive a Gift Card.");
			} else {
				and_receive_txt.setText("and receive up to a $"+tradeInValueString+" Gift Card.");//Html.fromHtml("and receive up to a $"+tradeInValueString+" Gift Card.<sup><small>2</small></sup>")			
			}
		}		
		if(getApp().getCarrierId()==R.id.linear_layout_at_t){
			carrierLogo.setImageResource(R.drawable.at_t_logo);
			upgrade_chkr_at_t_text.setVisibility(0);
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_verizon){
			carrierLogo.setImageResource(R.drawable.verizon_logo);
		 	upgrade_chkr_at_t_text.setVisibility(8);
		 
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_tmobile){
			carrierLogo.setImageResource(R.drawable.tmobile_icon);
			upgrade_chkr_at_t_text.setVisibility(8);
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_sprint){
			carrierLogo.setImageResource(R.drawable.sprint_logo);
			upgrade_chkr_at_t_text.setVisibility(8);
		}		
		if(upgradeEligibilityFlag){			
			good_news_text.setText(upgradeEligibilityMessage);		
			goodNewsFootNote.setText(upgradeEligibilityFootnote);
		}
		else{			
			good_news_text.setText(upgradeEligibilityMessage);	
		}
		phoneNumber.setText(subscriberPhoneNumber); 
 		eligibilityDate.setText(upgradeEligibilityDateString);
 		contractEndDate.setText(contractEndDateString);
 		if(subscriber.getUpgradeEligibilityDate().equals(""))
 		{
 			fullEligibilityText.setVisibility(View.GONE);
 		}
 		else if(subscriber.getContractEndDate().equals("")){
 			contractDateText.setVisibility(View.GONE);
 		}
 		
 		if(upgradeType.equals("ELIGIBLE_FULL")){							
			contact_layout.setVisibility(View.GONE);			
			textme_and_callme_layout.setVisibility(View.GONE);
			upg_txt.setText("UPGRADE YOUR PHONE");	
			will_call.setVisibility(View.GONE);
		}	
		 
	}
	private void getSubscriberDetails(Subscriber subscriber){
		subscriberPhoneNumber=subscriber.getMobilePhoneNumber();		
		zip= (String) subscriber.getZip();
		upgradeEligibilityDateString = (String) subscriber.getUpgradeEligibilityDate();
		contractEndDateString = (String) subscriber.getContractEndDate();	
		tradeInValueString = (String) subscriber.getTradeInValue();
		if(tradeInValueString != null && !tradeInValueString.equals("")){
			tradeInValueString = tradeInValueString.replace("$", "");
			DecimalFormat format = new DecimalFormat("#0.00");
			tradeInValueString = format.format(Double.valueOf(tradeInValueString));
		}
		upgradeEligibilityMessage = (String) subscriber.getUpgradeEligibilityMessage();
		upgradeEligibilityFootnote = (String) subscriber.getUpgradeEligibilityFootnote();
		upgradeEligibilityFlag =(boolean) subscriber.isUpgradeEligibilityFlag();
		getApp().setPrimaryPhoneNumber(subscriberPhoneNumber);
		getApp().setZipCode(zip);
	}	
	public void onClick(View view) {
		if(view==textMe){			
			notificationCode="TEXT";
			new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		else if(view==callMe){
			notificationCode="VOICE";
			new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		else if(view==findStore){
			if(upgradeEligibilityFlag){
				Intent findStoreIntent=new Intent(UpgradeCheckerTermsandConditions.this,StoreLocatorList.class);
				findStoreIntent.putExtra(StoreUtils.IS_OPENBOX, false);
				findStoreIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(findStoreIntent);
			}
		}
		else if(view==changeToCallMe){
			notificationCode="VOICE";
			new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		else if(view==changeToTextMe){
			notificationCode="TEXT";
			new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		else if(view==view_terms_con){
			Intent intent=new Intent(UpgradeCheckerTermsandConditions.this,TermsAndConditionsActivity.class);			
			startActivity(intent);
		}
	}
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) getApplicationContext();
	}	
	
	class PutNotificationTask extends BBYAsyncTask{

		public PutNotificationTask(Activity activity) {
			super(activity, "Updating Notification Status...");			
		}
		
		@Override
		public void doTask() throws Exception {
			PutNotificationService putNotificationService=new PutNotificationService(UpgradeCheckerTermsandConditions.this, notificationCode);
			results=putNotificationService.call();
		}
		
		@Override
		public void doFinish() {
			String errorCode=(String) results.get("error");
			removeDialog(PROGRESS_DIALOG);
			if(errorCode.equals("")){
				Intent intent=new Intent(UpgradeCheckerTermsandConditions.this,AllSetActivity.class);				
				UpgradeCheckerTermsandConditions.this.finish();
				startActivity(intent);				
			}	
			else{
				if(this == null || UpgradeCheckerTermsandConditions.this.isFinishing()) { // Use ||, Do not use | 
					return;
				}
				AlertDialog.Builder alert=new AlertDialog.Builder(UpgradeCheckerTermsandConditions.this);			
				alert.setMessage("Connectivity error. Please try again.");			
				alert.setNegativeButton("OK", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alert.create();
				alert.show();
			}
		}	
		
		@Override
		public void doReconnect() {
			new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
		
		
		@Override
		public void doError() {						
			HashMap results = getApp().getResults();
			if (results != null && results.get("error") != null ) {   
				if(results.get("error").equals("500")){
					new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
					return;
				}
				else{
					NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
						public void onReconnect() {
							new PutNotificationTask(UpgradeCheckerTermsandConditions.this).execute();
						}		
					}, new OnCancel() {

						public void onCancel() {
							finish();
						}
					});
				}
			}
		}
	}
	
	class GetNotificationStatusTask extends BBYAsyncTask{

		public GetNotificationStatusTask(Activity activity) {
			super(activity, "Getting Notification Status...");
			
		}
		
		@Override
		public void doTask() throws Exception {
			GetNotificationStatusService getNotificationStatus=new GetNotificationStatusService(UpgradeCheckerTermsandConditions.this);
			results=getNotificationStatus.call();
		}
		
		@Override
		public void doFinish() {
			String errorCode=(String) results.get("error");
			removeDialog(GET_STATUS_PROGRESS_DIALOG);
			if(errorCode.equals("")){
				notificationStatus=(String) results.get("status");
				initialiseComponents();
			}
			else{
				if(this == null || UpgradeCheckerTermsandConditions.this.isFinishing()) { // Use ||, Do not use | 
					return;
				}
				AlertDialog.Builder alert=new AlertDialog.Builder(UpgradeCheckerTermsandConditions.this);			
				alert.setMessage("Connectivity error. Please try again.");			
				alert.setNegativeButton("OK", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						 if(UpgradeChecker.SELECTED_CARRIER.equals("ATT")){
							 intent = new Intent(UpgradeCheckerTermsandConditions.this,
										ATandTUpgradeChecker.class);
						 }
						 else if(UpgradeChecker.SELECTED_CARRIER.equals("VERIZON")){
							 intent = new Intent(UpgradeCheckerTermsandConditions.this,
									 VerizonUpgradeChecker.class);					 
						 }
						 else if(UpgradeChecker.SELECTED_CARRIER.equals("TMOBILE")){
							 intent = new Intent(UpgradeCheckerTermsandConditions.this,
									 TMobileUpgradeChecker.class);
						 }
						 else if(UpgradeChecker.SELECTED_CARRIER.equals("SPRINT")){
							 intent = new Intent(UpgradeCheckerTermsandConditions.this,
									 SprintUpgradeChecker.class);
						 }
						 UpgradeCheckerTermsandConditions.this.finish();
						 startActivity(intent);
					}
				});
				alert.create();
				alert.show();
			}
		}
		
		@Override
		public void doReconnect() {
			new GetNotificationStatusTask(UpgradeCheckerTermsandConditions.this).execute();
		}
		
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
		
		
		@Override
		public void doError() {						
			HashMap results = getApp().getResults();
			if (results != null && results.get("error") != null ) {   
				if(results.get("error").equals("500")){
					new GetNotificationStatusTask(UpgradeCheckerTermsandConditions.this).execute();
					return;
				}
				else{
					NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
						public void onReconnect() {
							new GetNotificationStatusTask(UpgradeCheckerTermsandConditions.this).execute();
						}		
					}, new OnCancel() {

						public void onCancel() {
							finish();
						}
					});
				}
			}
		}
	}
	
	static class InternalURLSpan extends ClickableSpan {  
	    OnClickListener mListener;  	  
	    public InternalURLSpan(OnClickListener listener) {  
	        mListener = listener;  
	    }  
	  
	    @Override  
	    public void onClick(View widget) {  
	        mListener.onClick(widget);  
	    }  
	    public void updateDrawState(TextPaint p_DrawState) {	    	
	        super.updateDrawState(p_DrawState);	  	        
	        p_DrawState.setColor(Color.rgb(56, 114, 172));
	        p_DrawState.bgColor=Color.TRANSPARENT;
	        p_DrawState.setUnderlineText(false);	           
	    }
	}  
}