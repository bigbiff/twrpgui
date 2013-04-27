import java.io.File;

public class common {

	public static String getParentDir(String path) {
		File f = new File(path);
		return f.getParent();
	}
}

class MutableObject {
	   private String data = "";
	   private String arg = "";
	   
	   public String getData() {
	      return data;
	   }
	   
	   public String getArg() {
		   return arg;
	   }
	   
	   public void setData(String data) {
	      this.data = data;
	   }
	   
	   public void setArg(String arg) {
		   this.arg = arg;
	   }
	   
	   @Override
	   public String toString() {
	      return data;
	   }

}