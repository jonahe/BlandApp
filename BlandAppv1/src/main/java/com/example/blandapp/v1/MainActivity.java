package com.example.blandapp.v1;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Slider;
import com.gc.materialdesign.views.Slider.OnValueChangedListener;
import com.gc.materialdesign.widgets.Dialog;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/* 
 BlandApp - Fyll i volym och procenthalt på flera drycker och se hur starkt det blir. Få förslag på utspädning för att nå 
  
  */

public class MainActivity extends Activity  {
	
	Slider volumeSlider1, volumeSlider2, volumeSlider3;             
	Slider percentageSlider1, percentageSlider2, percentageSlider3;  
	
	ButtonFlat flatbuttonSuggestions;
	Button infoButton; 
	
	TextView tvInfoDrink1, tvInfoDrink2, tvInfoDrink3;
	TextView tvResult,tvTitleSecretButton;
	
	RelativeLayout mainLayout;
	LinearLayout layoutForGraphics, layoutForGraphics2, layoutForGraphics3;
	
	int CLvalue1,CLvalue2,CLvalue3;
	int PercValue1, PercValue2, PercValue3;
	
	BigDecimal CLpureAlcNr1,CLpureAlcNr2,CLpureAlcNr3;
	BigDecimal exactTotalVolume, roundedTotalPercentage;
	
	int widthOfLayout;
	
	boolean startedFromSavedState;
	String firstTimeTag = "firstTimeStarting?";
	boolean isItFirstTimeStarting;
	
	boolean isSpecialGraphicsOn;
	boolean isLargeDrink1On;
	
	String MyLogTag = "MyTag";
	String TagForVolumeList = "savedVolumeList"; // till onSaveInstance..
	String TagForPercentageList = "savedPercentageList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE); // tar bort "actionbar" som bara visar appnamnet annars
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        isItFirstTimeStarting = prefs.getBoolean(firstTimeTag, true);
        
        
        
        if (isItFirstTimeStarting) firstTimePopUp();
        
        
        Log.v(MyLogTag, "onCreate, körs");
        
        initializeViews();
        

        	
        	
        
        
        
        if (savedInstanceState != null) {

        	setUpSavedValues(savedInstanceState);
        }
        
    }
    



	private void initializeViews() {
		
		CLpureAlcNr1 = BigDecimal.ZERO;
		CLpureAlcNr2 = BigDecimal.ZERO;
		CLpureAlcNr3 = BigDecimal.ZERO;
		
		exactTotalVolume = BigDecimal.ZERO;
		roundedTotalPercentage = BigDecimal.ZERO;
		
		
		mainLayout			= (RelativeLayout)	findViewById(R.id.MainLayout);
		
		layoutForGraphics 	= (LinearLayout)	findViewById(R.id.layoutForGraphics);
		layoutForGraphics2 	= (LinearLayout)	findViewById(R.id.layoutForGraphics2);
		layoutForGraphics3 	= (LinearLayout) 	findViewById(R.id.layoutForGraphics3);
		
		flatbuttonSuggestions = (ButtonFlat)	findViewById(R.id.buttonflatMoreInfo);
		flatbuttonSuggestions.setTextColor(Color.parseColor("#effbff"));
		flatbuttonSuggestions.setBackgroundColor(Color.TRANSPARENT);
		flatbuttonSuggestions.setTextSize(15);
		
		
		int colorVolumeSliders = getResources().getColor(R.color.colorVolumeSlider);
    	volumeSlider1 = (Slider) findViewById(R.id.VolumeSlider1);
    	volumeSlider2 = (Slider) findViewById(R.id.VolumeSlider2);
    	volumeSlider3 = (Slider) findViewById(R.id.VolumeSlider3);
    	
    	
    	volumeSlider1.setBackgroundColor(colorVolumeSliders);
    	volumeSlider2.setBackgroundColor(colorVolumeSliders);
    	volumeSlider3.setBackgroundColor(colorVolumeSliders);
    	
    	
    	int colorPercentageSliders = getResources().getColor(R.color.colorPercentageSlider);
    	percentageSlider1 = (Slider) findViewById(R.id.PercentageSlider1);
    	percentageSlider2 = (Slider) findViewById(R.id.PercentageSlider2);
    	percentageSlider3 = (Slider) findViewById(R.id.PercentageSlider3);
    	
    	percentageSlider1.setBackgroundColor(colorPercentageSliders);
    	percentageSlider2.setBackgroundColor(colorPercentageSliders);
    	percentageSlider3.setBackgroundColor(colorPercentageSliders);
    	
    	percentageSlider1.showNumberIndicator(false);
    	percentageSlider2.showNumberIndicator(false);
    	percentageSlider3.showNumberIndicator(false);
    	volumeSlider1.showNumberIndicator(false);
    	volumeSlider2.showNumberIndicator(false);
    	volumeSlider3.showNumberIndicator(false);
    	
    	tvInfoDrink1 = (TextView) findViewById(R.id.infoDryck1);
    	tvInfoDrink2 = (TextView) findViewById(R.id.infoDryck2);
    	tvInfoDrink3 = (TextView) findViewById(R.id.infoDryck3);
    	
    	// eftersom xml-kommentarer är bökigt: färgscheman för dr1,2,3: originalet = 1:FF5722 2:FF8822 3: FFCCBC resultat:tranparent.. , colorVolumeSlider 0097A7, colorPercentageSlider 00BCD4
    	
    	
    	// tillåter användaren att ändra första dryckens maxgräns 
    	
    	isLargeDrink1On = false;
    	tvInfoDrink1.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View tvInfoDrink1) {
				
				if (isLargeDrink1On == false) {
					volumeSlider1.setMax(300);
					isLargeDrink1On = true;
					volumeSlider1.setValue(CLvalue1); // så att sliderns position stämmer överrens med nya värdet
					  // updaterade proportioner på drinkgrafik behövs inte eftersom grafiken baseras på aktuell cl1+cl2+cl3, inte den potientiella maxgränsen.
				Toast toast = Toast.makeText(getApplicationContext(),"Maxvärdet: 3 liter", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP|Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
					
				} else {
					volumeSlider1.setMax(100);  // tillbaka till grundvärdet
					isLargeDrink1On = false;
					if (CLvalue1 > 100) {  // eftersom den inte kan hålla mer än 100, får värdet "skalas ner". sedan uppmärksammas användaren om 
						CLvalue1 = 100; 	// detta genom att text och grafik ändras.
						volumeSlider1.setValue(CLvalue1);
						updateDrinkInfo(1);
						calculateSum();
						updateDrinkGraphics();
						
					} 
					volumeSlider1.setValue(CLvalue1);
					Toast toast = Toast.makeText(getApplicationContext(),"Maxvärdet: 1 liter", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP|Gravity.CENTER_VERTICAL, 0, 0);
					toast.show();
					
					
				}
				
				return false;
			}
    		
    	});
    	
    	
    	isSpecialGraphicsOn = false;
    	
    	tvTitleSecretButton = (TextView) findViewById(R.id.AppTitle);
    	
        tvTitleSecretButton.setOnClickListener(new OnClickListener() {  // den här ska bara gå mellan "av" och "på". värdet läses av i updateDrinkGraphics()

			@Override
			public void onClick(View arg0) {
			
			if (isSpecialGraphicsOn == false) {
				isSpecialGraphicsOn = true;
				} else { isSpecialGraphicsOn = false; }
			
			updateDrinkGraphics ();  // gör ändringen synlig på en gång. 
			
			}

        });
    	
    	
    	
    	
    	
    	tvResult = (TextView) findViewById(R.id.ResultatTextView);
    	
    	CLvalue1 = 0;
    	CLvalue2 = 0;
    	CLvalue3 = 0;
    	
    	PercValue1 = 0;
    	PercValue2 = 0;
    	PercValue3 = 0;
    	
    	Log.v(MyLogTag, "initializeViews, klar");
    	
    	setupListeners();
		
	}
    

	
	private void updateDrinkInfo (int drinkNr) {
		
		List<Integer> CLvalueList = Arrays.asList(CLvalue1,CLvalue2,CLvalue3); // "smidigt" sätt att få tillgång till alla CLvärden, utan att ändra dom i sluträkningen.
		int copyOfCLvalue = CLvalueList.get(drinkNr-1);
		
		int[] CL_DL_L = convertCLto_L_DL_and_CL(copyOfCLvalue); // skickar den aktuella drickans cl, får tillbaka tre nummer i array.
		
		String volumeMessage = makeVolumeMessage(CL_DL_L); // skickar l, dl och cl. får tillbaka string, typ "1 l, 2dl och 3cl"
		
		List<Integer> PercValueList = Arrays.asList(PercValue1,PercValue2,PercValue3); // hämtar procentvärdena
		int copyOfPercValue = PercValueList.get(drinkNr-1);	// plockar ut det aktuella procentvärdet.
		
		switch (drinkNr) {
		
		case 1 : tvInfoDrink1.setText(volumeMessage + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;
		case 2 : tvInfoDrink2.setText(volumeMessage + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;
		case 3 : tvInfoDrink3.setText(volumeMessage + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;

		}
		
		
		
		
		
		
		// detta är den gamla lösningen för att få infotext på varje dryck. 
//		List<Integer> PercValueList = Arrays.asList(PercValue1,PercValue2,PercValue3);
//		int copyOfPercValue = PercValueList.get(drinkNr-1);	
//
//		String appropriateUnit;
//		
//		if 		((copyOfCLvalue != 0) & (copyOfCLvalue % 100 == 0)) {
//			appropriateUnit = "liter";
//			copyOfCLvalue = (copyOfCLvalue/100);
//		}
//		else if ((copyOfCLvalue != 0) & (copyOfCLvalue % 10 == 0))	{
//			appropriateUnit = "dl";
//			copyOfCLvalue = (copyOfCLvalue/10);
//		}
//		else {appropriateUnit = "cl";}
//		
//		switch (drinkNr) {
//		
//		case 1 : tvInfoDrink1.setText(""+ copyOfCLvalue + " "+ appropriateUnit + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;
//		case 2 : tvInfoDrink2.setText(""+ copyOfCLvalue + " "+ appropriateUnit + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;
//		case 3 : tvInfoDrink3.setText(""+ copyOfCLvalue + " "+ appropriateUnit + " av dryck " + drinkNr + ".   " + copyOfPercValue + "% alkohol"); break;
//
//		}
		
	}
	
	private void calculateSum() {
		
		MathContext precision = MathContext.DECIMAL32; // behövs för att undvika "Non-terminating decimal expansion; no exact representable decimal result”.
		
		// koden nedan motsvarar 	double CLpureAlcNr1 = ((PercValue1*CLvalue1)/100); 
		CLpureAlcNr1 = ((new BigDecimal(PercValue1, precision).multiply(new BigDecimal (CLvalue1, precision)))).divide(new BigDecimal (100, precision).setScale(3, RoundingMode.HALF_UP)); // (procentsats * dryckvolym) / 100 = cl alk
		CLpureAlcNr2 = ((new BigDecimal(PercValue2, precision).multiply(new BigDecimal (CLvalue2, precision)))).divide(new BigDecimal (100, precision).setScale(3, RoundingMode.HALF_UP));
		CLpureAlcNr3 = ((new BigDecimal(PercValue3, precision).multiply(new BigDecimal (CLvalue3, precision)))).divide(new BigDecimal (100, precision).setScale(3, RoundingMode.HALF_UP));
		
	
		// koden nedan motsvarar  CLsumOfPureAlc = (CLpureAlcNr1+CLpureAlcNr2+CLpureAlcNr3);
		BigDecimal CLsumOfPureAlc = ((CLpureAlcNr1).add(CLpureAlcNr2).add(CLpureAlcNr3)).setScale(3, RoundingMode.HALF_UP);
		
		
		int totalVolume = (CLvalue1 + CLvalue2 + CLvalue3);
		exactTotalVolume = new BigDecimal(totalVolume); 
		
		if (totalVolume != 0) {
			// koden nedan motsvarar  unconvertedTotalPercentageAlc = (CLsumOfPureAlc / totalVolume);
			BigDecimal unconvertedTotalPercentageAlc = (CLsumOfPureAlc).divide(exactTotalVolume, precision).setScale(3, RoundingMode.HALF_UP);
			// ...   convertedTotalPercentageAlc = (unconvertedTotalPercentageAlc*100);
			BigDecimal convertedTotalPercentageAlc = (unconvertedTotalPercentageAlc).multiply(new BigDecimal(100, precision).setScale(3, RoundingMode.HALF_UP));
			roundedTotalPercentage = convertedTotalPercentageAlc.setScale(1, RoundingMode.HALF_UP);
			
			tvResult.setText("Blandningens alkoholhalt är "+roundedTotalPercentage +"%");
			

			
		}	
	}
	
    private void setUpSavedValues(Bundle savedInstanceState) {
		
    	startedFromSavedState = true;
    	
    	int[] savedVolumeList = savedInstanceState.getIntArray(TagForVolumeList);
		int[] savedPercentageList = savedInstanceState.getIntArray(TagForPercentageList);
		isLargeDrink1On = savedInstanceState.getBoolean("isLargeDrink1On");
		isSpecialGraphicsOn = savedInstanceState.getBoolean("isSpecialGraphicsOn");
		
		CLvalue1 = (savedVolumeList[0]);
		CLvalue2 = (savedVolumeList[1]);
		CLvalue3 = (savedVolumeList[2]);
		
		PercValue1 = (savedPercentageList[0]);
		PercValue2 = (savedPercentageList[1]);
		PercValue3 = (savedPercentageList[2]);
		
		
		if (isLargeDrink1On == true) {
			
			volumeSlider1.setMax(300);
		}
		
    	volumeSlider1.setValue(savedVolumeList[0]);
    	volumeSlider2.setValue(savedVolumeList[1]);
    	volumeSlider3.setValue(savedVolumeList[2]);
    	
    	percentageSlider1.setValue(savedPercentageList[0]);
    	percentageSlider2.setValue(savedPercentageList[1]);
    	percentageSlider3.setValue(savedPercentageList[2]);
    	
    	updateDrinkInfo(1);
    	updateDrinkInfo(2);
    	updateDrinkInfo(3);
    	
    	calculateSum();
    	
    	// timer som ser till att updateDrinkGraphics inte uppdateras för tidigt, då ger getWidth 0, vilket ger en exception senare.
    	new CountDownTimer(10, 10) {

    	     public void onTick(long millisUntilFinished) {
    	         
    	     }

    	     public void onFinish() {
    	    	 updateDrinkGraphics ();
    	     }
    	  }.start();


    	
		
	}
    
    
	private void updateDrinkGraphics () {
		
		
		int colorDrink1, colorDrink2, colorDrink3;
		colorDrink1 = getResources().getColor(R.color.colorDrink1);
		colorDrink2 = getResources().getColor(R.color.colorDrink2);
		colorDrink3 = getResources().getColor(R.color.colorDrink3);
		
		
		Paint paintDrink1 = new Paint();
		Paint paintDrink2 = new Paint();
		Paint paintDrink3 = new Paint();
		paintDrink1.setColor(colorDrink1);
		paintDrink2.setColor(colorDrink2);
		paintDrink3.setColor(colorDrink3);
		
		// graferna behöver vara olika "breda" beroende på mobilens orientering. Annars får de inte plats (syns inte).
		int bitmapHeight = 0;
		int rotation = getWindowManager().getDefaultDisplay().getRotation();  // 0 = upprätt, 1 och 3 är landscape 
		
		if (rotation == 1 || rotation == 3) bitmapHeight= 25;
		else bitmapHeight= 70;
		
		
		
		widthOfLayout = layoutForGraphics.getWidth(); // hämtar bredden från en av de linearLayouts som ska innehålla staplar
		// widthOfLayout = 300;
		Log.v(MyLogTag, "getWidth = " +widthOfLayout);
		
		
		// kanske kan skapa dessa via Bitmap bitmap1,bitmap2,bitmap3; högst upp, och sen ha en  if (bitmap1 == null/empty  || bitmap2... || bitmap3..)
		// . är osäker på ifall dom skapas om och om igen, istället för att bara uppdateras.
		
		Bitmap bitmap1 = Bitmap.createBitmap(widthOfLayout, bitmapHeight, Bitmap.Config.ARGB_8888);
		Bitmap bitmap2 = Bitmap.createBitmap(widthOfLayout, bitmapHeight, Bitmap.Config.ARGB_8888);
		Bitmap bitmap3 = Bitmap.createBitmap(widthOfLayout, bitmapHeight, Bitmap.Config.ARGB_8888);
		
//		Bitmap bitmap1,bitmap2,bitmap3;
//		bitmap1 = null;
//		bitmap2 = null;
//		bitmap3 = null;
		
		
//		if (calculateDrinkPixelSize(1) == 0) {
//			bitmap1 = Bitmap.createBitmap(20, bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
//		else {
//			bitmap1 = Bitmap.createBitmap(calculateDrinkPixelSize(1), bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
//		if (calculateDrinkPixelSize(2) == 0) {
//			bitmap2 = Bitmap.createBitmap(20, bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
//		else {
//			bitmap2 = Bitmap.createBitmap(calculateDrinkPixelSize(2), bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
//		if (calculateDrinkPixelSize(3) == 0) {
//			 bitmap3 = Bitmap.createBitmap(20, bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
//		else {
//		 bitmap3 = Bitmap.createBitmap(calculateDrinkPixelSize(3), bitmapHeight, Bitmap.Config.ARGB_8888);
//		}
		
		Canvas canvas1,canvas2,canvas3;
		canvas1= null;
		canvas2= null;
		canvas3= null;
		
//		Canvas canvas1 = new Canvas(bitmap1);
//		Canvas canvas2 = new Canvas(bitmap2);
//		Canvas canvas3 = new Canvas(bitmap3);
		
		canvas1 = new Canvas(bitmap1);
		canvas2 = new Canvas(bitmap2);
		canvas3 = new Canvas(bitmap3);
		
		// fyll canvas med "transarent" blir som en clear. annars läggs alla bilder på varandra. 
		
		canvas1.drawColor(Color.TRANSPARENT);
		canvas2.drawColor(Color.TRANSPARENT);
		canvas3.drawColor(Color.TRANSPARENT);
		
		if (isSpecialGraphicsOn == false) {  // standardgrafiken. 
			
			canvas1.drawRect(0, 0, calculateDrinkPixelSize(1), bitmapHeight, paintDrink1); // start x, start y, längd x, längd y 
			layoutForGraphics.setBackground(new BitmapDrawable(getResources(), bitmap1));
			
			canvas2.drawRect(0, 0, calculateDrinkPixelSize(2), 100, paintDrink2); // start x, start y, längd x, längd y 
			layoutForGraphics2.setBackground(new BitmapDrawable(getResources(), bitmap2));
			
			canvas3.drawRect(0, 0, calculateDrinkPixelSize(3), 100, paintDrink3); // start x, start y, längd x, längd y 
			layoutForGraphics3.setBackground(new BitmapDrawable(getResources(), bitmap3));
			
		} else {
		
			// den här varianten bildar en "trappa" av graferna. den ena börjar där den tidigare slutar..
			
			canvas1.drawRect(0, 0, calculateDrinkPixelSize(1), bitmapHeight, paintDrink1); // start x, start y, längd x, längd y 
			layoutForGraphics.setBackground(new BitmapDrawable(getResources(), bitmap1));
			
			canvas2.drawRect(calculateDrinkPixelSize(1), 0, calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2), bitmapHeight, paintDrink2); // start x, start y, längd x, längd y 
			layoutForGraphics2.setBackground(new BitmapDrawable(getResources(), bitmap2));
			
			canvas3.drawRect(calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2), 0, calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2) + calculateDrinkPixelSize(3), bitmapHeight, paintDrink3); // start x, start y, längd x, längd y 
			layoutForGraphics3.setBackground(new BitmapDrawable(getResources(), bitmap3));
			
		}
		

		
		

		
		// kod nedan skulle få in "trappan" i en och samma längd. visar bara sista stapeln.
		
//		canvas1.drawRect(0, 0, calculateDrinkPixelSize(1), bitmapHeight, paintDrink1); // start x, start y, längd x, längd y 
//		layoutForGraphics.setBackground(new BitmapDrawable(getResources(), bitmap1));
//		
//		canvas2.drawRect(calculateDrinkPixelSize(1), 0, calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2), bitmapHeight, paintDrink2); // start x, start y, längd x, längd y 
//		layoutForGraphics.setBackground(new BitmapDrawable(getResources(), bitmap2));
//		
//		canvas3.drawRect(calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2), 0, calculateDrinkPixelSize(1) + calculateDrinkPixelSize(2) + calculateDrinkPixelSize(3), bitmapHeight, paintDrink3); // start x, start y, längd x, längd y 
//		layoutForGraphics.setBackground(new BitmapDrawable(getResources(), bitmap3));

		
		
		
		// layoutForGraphics.set (new BitmapDrawable(bitmap));
		
		Log.v(MyLogTag, "getWidth är = " + widthOfLayout);
		//Log.v(MyLogTag, "CL1percentageOfTotal är = " + CL1percentageOfTotal);
		Log.v(MyLogTag, "pixelLenghtofDrink1 är = " + calculateDrinkPixelSize(1));
		Log.v(MyLogTag, "pixelLenghtofDrink2 är = " + calculateDrinkPixelSize(2));
		
		
		
	}
	
	private int calculateDrinkPixelSize (int drinkNr) {
		int size = 0;
		int totalVolume = (CLvalue1+CLvalue2+CLvalue3);
		MathContext precision = MathContext.DECIMAL32;
		BigDecimal CLpercentageOfTotal;
		BigDecimal pixelLenghtofDrink;
		
		if (totalVolume != 0) {
		
		switch (drinkNr) {
		
		case 1: CLpercentageOfTotal = new BigDecimal(CLvalue1, precision).divide(new BigDecimal(totalVolume), precision); break;
		case 2: CLpercentageOfTotal = new BigDecimal(CLvalue2, precision).divide(new BigDecimal(totalVolume), precision); break;
		case 3: CLpercentageOfTotal = new BigDecimal(CLvalue3, precision).divide(new BigDecimal(totalVolume), precision); break;
		default: CLpercentageOfTotal = new BigDecimal(0, precision).divide(new BigDecimal(0), precision); break; // om något går fel ska det synas på siffrorna
		}
		
		pixelLenghtofDrink = new BigDecimal(widthOfLayout, precision).multiply(CLpercentageOfTotal, precision);
		pixelLenghtofDrink = pixelLenghtofDrink.setScale(0, RoundingMode.HALF_DOWN);
		
		size = pixelLenghtofDrink.intValue();
		}
		
		
		return size;
		
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		

		
		int[] savedVolumeList = {CLvalue1,CLvalue2,CLvalue3};
		int[] savedPercentageList = {PercValue1,PercValue2,PercValue3};
		
		
		outState.putIntArray(TagForVolumeList, savedVolumeList);
		outState.putIntArray(TagForPercentageList, savedPercentageList);
		outState.putBoolean("isLargeDrink1On", isLargeDrink1On); // skickar med huruvida maxvärdet är höjt
		outState.putBoolean("isSpecialGraphicsOn", isSpecialGraphicsOn);
		
	}
	
    @Override
    protected void onPause()
    {
    super.onPause();
    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(firstTimeTag, false);
    editor.commit();
    }
    
    private void firstTimePopUp () {
    	
    	// första gången appen startar.
    	
    	
    	Dialog dialog = new Dialog(this,"BlandApp - Första gången?", "Instruktion: Justera dragen efter dina ingredienser eller tills du uppnått önskad alkoholhalt. \n" +
    			"Tips 1: 'långklicka' på dryck 1-texten för att öka maxvärdet till 3 liter. \n" +
    			"Tips 2: För starkt? Klicka på 'Förslag' för att få tips på olika utblandningar. \n" +
    			"Tips 3: Klicka på app-titeln för att ändra hur 'graferna' ser ut.");

    	dialog.show();
    	ButtonFlat acceptButton = dialog.getButtonAccept();
    	ButtonFlat cancelButton = dialog.getButtonCancel();
    	
    	cancelButton.setText(""); // setVisibility funkade inte
    	acceptButton.setText("OK");
    	

    	
    	
    }
    
    public void onMoreSuggestionsClick (View view)  {
    	
    	Log.v(MyLogTag, "FörslagsKnappen intryckt");
    	MathContext precision = MathContext.DECIMAL32;
    	
    	Log.v(MyLogTag, "" + roundedTotalPercentage.compareTo(new BigDecimal (7,precision)));
    	
    	// 1 if this > val, -1 if this < val, 0 if this == val.  Om compare ger -1 är roundedTotalPercentage mindre än 7. (+)1 ifall den är större. se nedan.
    	
    	if (exactTotalVolume == BigDecimal.ZERO || exactTotalVolume == null || (roundedTotalPercentage.compareTo(new BigDecimal (5.1,precision )) == -1)) { 
    		

    		Dialog dialog = new Dialog(this,"För lite alkohol..", "Kan inte föreslå något om alkoholhalten redan är mindre än 5%");
        	dialog.show();
        	ButtonFlat acceptButton = dialog.getButtonAccept();
        	ButtonFlat cancelButton = dialog.getButtonCancel();
        	
        	cancelButton.setText(""); // setVisibility funkade inte
        	acceptButton.setText("OK");
    		
    	}
    	
    	else {
    		// formel för att få fram den mäng blanddricka som behövs är (totala cl ren alk / önskad slutmängd t.ex 0.05) - totalvolym 
    		
    		String suggestionMessage = "";
    		
    		BigDecimal alcGoal = new BigDecimal (0.05, precision);
    		BigDecimal totalPureAlc = CLpureAlcNr1.add(CLpureAlcNr2).add(CLpureAlcNr3);
    		
    		BigDecimal neededDrink4value = (((totalPureAlc.divide(alcGoal, precision)).round(precision)).subtract(exactTotalVolume,precision)).setScale(0, RoundingMode.HALF_UP);

    		//suggestionMessage = ("Späd ut med alkoholfritt \n\n"+"För 5%   :  lägg till " + neededDrink4value + " cl ");
    		
    		suggestionMessage = ("Späd ut med alkoholfritt.\n\n"+"För  :  Lägg till \n"+"5%   :  " + makeVolumeMessage(convertCLto_L_DL_and_CL(neededDrink4value.intValueExact())));
    		
    		
    		// här kollas så att vi inte får tips som ger negativa värden. om den aktuella alkoholhalten är över en viss gräns kan användaren få tips om alkoholhalter under den gränsen.
    		
    		if ((roundedTotalPercentage.compareTo(new BigDecimal (7,precision )) == 1)) {
    			
    			alcGoal = new BigDecimal (0.07, precision);
    			neededDrink4value = (((totalPureAlc.divide(alcGoal, precision)).round(precision)).subtract(exactTotalVolume,precision)).setScale(0, RoundingMode.HALF_UP);
    			 // suggestionMessage = suggestionMessage+"\n"+("För 7%   :  lägg till " + neededDrink4value + " cl");
    			suggestionMessage = suggestionMessage+"\n"+("7%   :  " + makeVolumeMessage(convertCLto_L_DL_and_CL(neededDrink4value.intValueExact()))); // fixar l, dl, cl.
    			 
    			 Log.v(MyLogTag, "roundedTotalPercentage = " + roundedTotalPercentage);
    		}
    		
    		if ((roundedTotalPercentage.compareTo(new BigDecimal (9,precision )) == 1)) {
    			
    			alcGoal = new BigDecimal (0.09, precision);
    			neededDrink4value = (((totalPureAlc.divide(alcGoal, precision)).round(precision)).subtract(exactTotalVolume,precision)).setScale(0, RoundingMode.HALF_UP);
    			 //suggestionMessage = suggestionMessage+"\n"+("För 9%   :  lägg till " + neededDrink4value + " cl");  // det nya meddelandet läggs bara till till det gamla.
    			suggestionMessage = suggestionMessage+"\n"+("9%   :  " + makeVolumeMessage(convertCLto_L_DL_and_CL(neededDrink4value.intValueExact())));
    			 
    			 Log.v(MyLogTag, "roundedTotalPercentage = " + roundedTotalPercentage);
    		}
    		
    		if ((roundedTotalPercentage.compareTo(new BigDecimal (11,precision )) == 1)) {
        			
        			alcGoal = new BigDecimal (0.11, precision);
        			neededDrink4value = (((totalPureAlc.divide(alcGoal, precision)).round(precision)).subtract(exactTotalVolume,precision)).setScale(0, RoundingMode.HALF_UP);
        			//suggestionMessage = suggestionMessage+"\n"+("För 11% :  lägg till " + neededDrink4value + " cl");
        			suggestionMessage = suggestionMessage+"\n"+("11% :  " + makeVolumeMessage(convertCLto_L_DL_and_CL(neededDrink4value.intValueExact())));
    		}
    		
    		if ((roundedTotalPercentage.compareTo(new BigDecimal (15,precision )) == 1)) {
    			
    			alcGoal = new BigDecimal (0.15, precision);
    			neededDrink4value = (((totalPureAlc.divide(alcGoal, precision)).round(precision)).subtract(exactTotalVolume,precision)).setScale(0, RoundingMode.HALF_UP);
    			//suggestionMessage = suggestionMessage+"\n"+("För 15% :  lägg till " + neededDrink4value + " cl");
    			suggestionMessage = suggestionMessage+"\n"+("15% :  " + makeVolumeMessage(convertCLto_L_DL_and_CL(neededDrink4value.intValueExact())));
    		}
    		
    		
    		Dialog dialog = new Dialog(this,"Förslag?", ""+suggestionMessage);
        	dialog.show();
        	
        	ButtonFlat acceptButton = dialog.getButtonAccept();
        	ButtonFlat cancelButton = dialog.getButtonCancel();
        	
        	cancelButton.setText(""); // setVisibility funkade inte
        	acceptButton.setText("OK");
    		
        	Log.v(MyLogTag, "totalPureAlc = " +totalPureAlc +"\n neededDrink4value = "+ neededDrink4value);
    		}
    	

    	}
    	
		public void onInfoButtonClick (View view) {
		
	    	Dialog dialog = new Dialog(this,"BlandApp - Info/Hjälp", "Instruktion: Justera dragen efter dina ingredienser eller tills du uppnått önskad alkoholhalt. \n" +
	    			"Tips 1: 'långklicka' på dryck 1-texten för att öka maxvärdet till 3 liter. \n" +
	    			"Tips 2: För starkt? Klicka på 'Förslag' för att få tips på olika utblandningar. \n" +
	    			"Tips 3: Klicka på app-titeln för att ändra hur 'graferna' ser ut.");
	
	    	dialog.show();
	    	ButtonFlat acceptButton = dialog.getButtonAccept();
	    	ButtonFlat cancelButton = dialog.getButtonCancel();
	    	
	    	cancelButton.setText(""); // setVisibility funkade inte
	    	acceptButton.setText("OK");
	    	
			
		}
		
		
		
		
		
		
		
		
		
		
		// testversion av konverterare!  KOPPLA till UPDATEdrinkInfo
		
		
		
		
		
		
		
		
		private int[] convertCLto_L_DL_and_CL (int startingcl) {
			
			int centiliter = startingcl;
			int liter = 0;
			int deciliter =0;
			
			if (centiliter >= 100) {
				liter = (int) (centiliter/100); // t.ex. 265 blir 2,65. cast till (int) blir 2.
				centiliter = centiliter - (liter * 100); // tex. cl = 265 - (2*100)   ger 65  - detta för att kunna fortsätta nästa if-sats.	
			}
			if (centiliter >= 10) {
				deciliter = (int) (centiliter/10); // t.ex. 65 blir 6,5.  cast till (int) blir 6.
				
			}
			centiliter = centiliter - (deciliter * 10); // t.ex. cl = 65 - (6*10). blir 5   - läggs utanför if-satsen så att cl får rätt värde även om ingen av if-satserna aktiverats pga litet tal, t.ex. 9 cl.
			
			// allt har fått värden nu.  skicka tillbaka dom!
			
			
			int[] result = new int[3];
			result[0]	= liter;
			result[1]	= deciliter;
			result[2]	= centiliter;
			
			
			
		
			
			
			
			return result;
		}
		
		private String makeVolumeMessage (int[] l_dl_cl) {
			
			String message ="";
			
			int liter			= l_dl_cl[0];
			int deciliter 		= l_dl_cl[1];	
			int centiliter 		= l_dl_cl[2];
			
			// test av filter så att endast de värden över 0 visas. ska ej vara i denna metod egentligen. komplext att fixa.
			// tänkte fixa bakifrån, så att message först t.ex. får "och 3 cl". men bara ifall cl har ett värde.. 
			
			
			
			if ( (deciliter != 0 || liter !=0) && centiliter != 0 ) {  // ifall cl har värde över 0 och inte är det enda värdet över 0. då kan "och .. cl" läggas till som slut på message.
				
				message = " och "+ centiliter +" cl";
				
				if (deciliter !=0 && liter != 0) { // både dl och l har värden, och cl också (pga första if-satsen) --> skriv ut l och dl och lägg på den tidigare strängen ("och .. cl").
					
					message = ""+ liter +" l, " +deciliter +" dl" +message;
					}
				else if (deciliter !=0) {  // om dl inte är 0 så måste liter vara 0 (pga if-sats ovan)  --> skriv ut dl och lägg till tidigare strängen ("och .. cl").	
					message = ""+deciliter +" dl" +message;
				}
				else {  // sista fallet:  liter har värde, ej dl.  --> skriv ut dl och lägg till tidigare strängen ("och .. cl").
					message = ""+liter +" l" +message;
				}
			}
			
			// täckta fall hittills:  1 liter, 2 dl och 3 cl. ,  1 liter och 3 cl. ,   2 dl och 3cl.
			
			else if ( centiliter != 0 ) {  // fångar ifall dl och l båda är noll, men cl inte är noll. måste fångas innan de andra.. ?
				
				message = ""+ centiliter +" cl";
				
			}
			
			else if ( centiliter == 0 && deciliter == 0 && liter == 0 ) {  // fångar ifall alla är noll, då vill jag att enheten ska vara cl. 0 cl.
				
				message = ""+ centiliter +" cl";
				
			}
					
			else if (deciliter !=0 && liter != 0) { // cl är lika med 0 (pga första if-satsen). om l och dl har värden: message = "x liter och y dl"
				
				message = ""+ liter +" l och " + deciliter + " dl";
			}
			
			else if (deciliter == 0) { // då finns bara liter:  skriv ut liter 
				
				message = "" +liter +" l";
			}	
			else { // det ska bara finnas en kvar, då dl INTE är lika med noll: skriv ut dl. 
					
					message = "" +deciliter +" dl";
			}
			
			
			
			// fall ej täckta:  bara cl. bara dl. bara liter.
			
			Log.v(MyLogTag, message);
			
			//  Log.v(MyLogTag, ""+ (result[0]) +" liter, "+ (result[1]) +" deciliter och "+ (result[2]) +" centiliter!");
			
			
			return message;
		}
		
    
    	private void setupListeners() {  
		
		// kan ej använda case/switch pga onValueChange skickar inte med Slider och progress, utan bara progress. 
		// dvs inget sätt att skilja på vilken slider som skickar vad.
		
		Log.v(MyLogTag, "setupListeners, startad");
		
		infoButton = (Button) findViewById(R.id.buttonInfo);
 	
		volumeSlider1.setOnValueChangedListener(new OnValueChangedListener() {
		
		@Override
		public void onValueChanged(int value) {
		
		CLvalue1 = value; 
		
		calculateSum();
		updateDrinkInfo(1); //  detta är det enda som skiljer sig mellan de olika lyssnarna. skickar 1,2 eller 3 (för dryck 1,2,3).  Förrutom just 1:an som "Lyssnar" efter när slidern är vid 100.
		updateDrinkGraphics ();
		
		if (isLargeDrink1On == false & value == 100) {	// få infoknappen att blinka, för att påminna om att dryck1 kan öka maxläget. OM den inte redan är i max300-läget.
			
	    	
	    	new CountDownTimer(400, 80) {
	    		
	    		Boolean infoButtonNormal = true;
	    		
	    		
	    	     public void onTick(long millisUntilFinished) { // växla fram och tillbaka mellan turkosa och den vanliga originalfärgen, för att dra till uppmärksamhet.
	    	         
	    	    	 if (infoButtonNormal == true) {  // den komplicerade koden behövs för att button använder sig av något mer avancerat än bara en bakgrundsfärg.
	    	    		 
	    	    		 Drawable d = infoButton.getBackground();
	    	               PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
	    	               d.setColorFilter(filter);
	    	    		
	    	    		infoButtonNormal = false; 
	    	    	 } else {
	    	    		
	    	    		 Drawable d = infoButton.getBackground();
	    	    		 infoButton.invalidateDrawable(d);
	    	    		 d.clearColorFilter();
	    	    		 // infoButton.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);  // originalfilen är en gradient, inte transparent.
	    	    		 infoButtonNormal = true;
	    	    	 }
	    	    	 
	    	     }

	    	     public void onFinish() {
	    	    	 
	    	    	 Drawable d = infoButton.getBackground();
    	    		 infoButton.invalidateDrawable(d);
    	    		 d.clearColorFilter();
	    	    	 
	    	    	 //infoButton.setBackgroundResource(android.R.drawable.button_onoff_indicator_off); // se till att den alltid avslutar i sitt grundläge.
	    	    	 infoButtonNormal = true;
	    	     }
	    	  }.start();
			
			
		}
		
		}
		
		});
		
		volumeSlider2.setOnValueChangedListener(new OnValueChangedListener() {
			
		@Override
		public void onValueChanged(int value) {
		
		CLvalue2 = value; 
		
		calculateSum();
		updateDrinkInfo(2);
		updateDrinkGraphics ();
		
		}
		
		});
		

		volumeSlider3.setOnValueChangedListener(new OnValueChangedListener() {
		
		@Override
		public void onValueChanged(int value) {
		
		CLvalue3 = value; 
		
		calculateSum();
		updateDrinkInfo(3);
		updateDrinkGraphics ();
		
		}
		
		});
		
		percentageSlider1.setOnValueChangedListener(new OnValueChangedListener() {	
			
		@Override
		public void onValueChanged(int value) {
		
		PercValue1 = value; 
		
		calculateSum();	
		updateDrinkInfo(1);	
		updateDrinkGraphics ();
		
		}
		
		});
		
		Log.v(MyLogTag, "4 listener, avslutad");
		
		
		percentageSlider2.setOnValueChangedListener(new OnValueChangedListener() {	
			
		@Override
		public void onValueChanged(int value) {
		
		PercValue2 = value; 
		
		calculateSum();	
		updateDrinkInfo(2);	
		updateDrinkGraphics ();
		
		}
		
		});
		Log.v(MyLogTag, "5 listener, avslutad");
		
		
		percentageSlider3.setOnValueChangedListener(new OnValueChangedListener() {	
			
		@Override
		public void onValueChanged(int value) {
		
		PercValue3 = value; 
		
		calculateSum();
		updateDrinkInfo(3);
		updateDrinkGraphics ();
		 
		}
		
		});
		
		Log.v(MyLogTag, "setupListeners, avslutad");
		
		
	}
    



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
