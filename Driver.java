import com.firebase.client.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Driver
{
  public static final Firebase ref = new Firebase("https://sunsspot.firebaseio.com").child("users").child("cd1ced31-576e-4dc2-ae41-c81adc2826d4").child("data");
  public static final Firebase fridgeRef = ref.child("fridge");
  public static final Firebase cdRef = fridgeRef.child("consecutiveDehydration");
  public static final Firebase lqRef = fridgeRef.child("liquidsConsumedToday");
  public static final Firebase dlrRef = fridgeRef.child("dailyLiquidsRecommended");
  public static final Firebase alertRef = fridgeRef.child("alert");

  public static DataSnapshot uid = null;
  public static DataSnapshot liquidConsumedToday = null;
  public static DataSnapshot dailyLiquidRecommended = null;

  public static void main(String args[]){

    //Check if daily liquid intake is below threshold
    if(getLiquidConsumedTodayValue() < getDailyLiquidRecommendedValue()){
      System.out.println("Daily liquid intake is below threshold");
      System.out.println("Consumed today: " + getLiquidConsumedTodayValue());
      System.out.println("Recommended: " + getDailyLiquidRecommendedValue());

      incrementConsecutiveDehydrationValueByOne();
    }else{
      System.out.println("Daily liquid intake is above threshold");
      System.out.println("Consumed today: " + getLiquidConsumedTodayValue());
      System.out.println("Recommended: " + getDailyLiquidRecommendedValue());
    }

    //Check if consecutive dehydration value is consistently below threshold (for 3 days)
    if(getConsecutiveDehydrationValue()  >= 3){

      System.out.println("Consecutive dehydration value is consistently below threshold for " + getConsecutiveDehydrationValue() + " days");

      //Notify emergency services (And beep)
      notifyEmergencyServices();
      System.out.println("Emergency notified.");
    }else{
      System.out.println("Mary is doing fine. :)");
    }
    //Reset liquid consumed today
    resetLiquidConsumedToday();

    System.exit(0);

    // testGetConsecutiveDehydrationValue();
    // testGetLiquidConsumedTodayValue();
    // testGetLiquidRecommendedValue();
    // testIncrementConsecutiveDehydrationValueByOne();
    // testIsBelowThreshold();
    // testNotifyEmergencyServices();
    // testNotNotifyEmergencyServices();
    // testResetLiquidConsumedToday();

  };

  //---------- TEST CASES STARTS ----------//
  // Test if gets the consecutive dehydration value from Firebase
  @Test
  public static void testGetConsecutiveDehydrationValue(){
    // fail();
    System.out.println("Inside testGetConsecutiveDehydrationValue()");
    assertNotNull(getConsecutiveDehydrationValue());
  }

  // Test if gets the consecutive dehydration value from Firebase
  @Test
  public static void testGetLiquidConsumedTodayValue(){
    // fail();
    System.out.println("Inside testGetLiquidConsumedTodayValue()");
    assertNotNull(getConsecutiveDehydrationValue());
  }

  // Test if gets the consecutive dehydration value from Firebase
  @Test
  public static void testGetLiquidRecommendedValue(){
    // fail();
    System.out.println("Inside testGetLiquidRecommendedValue()");
    assertNotNull(getConsecutiveDehydrationValue());
  }

  // Test if it increases consecutive dehydration value by 1
  @Test
  public static void testIncrementConsecutiveDehydrationValueByOne(){
    // fail();
    System.out.println("Inside testIncrementConsecutiveDehydrationValueByOne()");
    int oldValue = getConsecutiveDehydrationValue();
    incrementConsecutiveDehydrationValueByOne();
    int newValue = getConsecutiveDehydrationValue();
    assertTrue(newValue > oldValue);
  }

  @Test
  public static void testIsBelowThreshold(){
    // fail();
    System.out.println("Inside testBelowThreshold()");
    assertTrue(isBelowThreshold());
  }

  @Test
  public static void testNotifyEmergencyServices(){
    // fail();
    System.out.println("Inside testNotifyEmergencyServices()");
    notifyEmergencyServices();
    assertTrue(getAlert());
  }

  @Test
  public static void testNotNotifyEmergencyServices(){
    // fail();
    System.out.println("Inside testNotNotifyEmergencyServices()");
    notNotifyEmergencyServices();
    assertFalse(getAlert());
  }

  @Test
  public static void testResetLiquidConsumedToday(){
    //fail();
    System.out.println("Inside testResetLiquidConsumedToday()");
    assertEquals(0, getLiquidConsumedTodayValue());
  }



  //---------- TEST CASES ENDS ----------//


  //---------- FUNCTION STARTS ----------//
  public static int getConsecutiveDehydrationValue(){
    return (int)((long) download(cdRef).getValue());
  }
  public static int getLiquidConsumedTodayValue(){
    return (int)((long) download(lqRef).getValue());
  }
  public static int getDailyLiquidRecommendedValue(){
    return (int)((long) download(dlrRef).getValue());
  }
  public static Boolean getAlert(){
    return (Boolean)((Boolean) download(alertRef).getValue());
  }
  public static void incrementConsecutiveDehydrationValueByOne(){
    int cdValue = getConsecutiveDehydrationValue();
    cdValue += 1;
    cdRef.setValue(cdValue);
  }
  public static Boolean isBelowThreshold(){
      return (getLiquidConsumedTodayValue() < getDailyLiquidRecommendedValue());
  }
  public static void notifyEmergencyServices(){
    Boolean alert = getAlert();
    alertRef.setValue(!alert);
  }
  public static void notNotifyEmergencyServices(){
    alertRef.setValue(false);
  }
  public static void resetLiquidConsumedToday(){
    lqRef.setValue(0);
  }

  //---------- FUNCTION ENDS ----------//

  public static DataSnapshot download(Firebase location)
  {
    final DataSnapshot returnValue[] = {null};

    ValueEventListener listener = new ValueEventListener()
    {
      @Override
      public void onDataChange(DataSnapshot snapshot)
      { returnValue[0] = snapshot; }

      @Override
      public void onCancelled(FirebaseError firebaseError)
      { System.out.println("The read failed: " + firebaseError.getMessage()); }
    };

    location.addListenerForSingleValueEvent(listener);
    location.removeEventListener(listener);

    for(int i = 0; returnValue[0] == null; i++)
    {
      try { Thread.sleep(100); }
      catch(InterruptedException e)
      { e.printStackTrace(); }

      if(i > 150)
        return null;
    }

    if(returnValue[0].exists())
      return returnValue[0];
    return null;
  }
}
