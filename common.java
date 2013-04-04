import java.io.File;

public class common {

	public static String getParentDir(String path) {
		File f = new File(path);
		return f.getParent();
	}
}

class MutableObject {
	   private String data = "";

	   public String getData() {
	      return data;
	   }

	   public void setData(String data) {
	      this.data = data;
	   }

	   @Override
	   public String toString() {
	      return data;
	   }

}