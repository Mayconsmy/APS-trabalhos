import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
public class ShiftLabel {
  public static void main(String[] a) throws Exception {
    ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
    pa.open(a[0], true, false, true);
    TransactionManager.beginTransaction();
    for (IDiagram d : pa.getProject().getDiagrams()) {
      if (!d.getName().startsWith("Questao 03")) continue;
      for (IPresentation p : d.getPresentations()) {
        if (p instanceof ILinkPresentation && p.getLabel() != null && p.getLabel().contains("ItemDoCarrinho")) {
          System.out.println("before " + p.getLabel() + " x=" + p.getProperty("name.point.x") + " y=" + p.getProperty("name.point.y"));
          double x = Double.parseDouble(p.getProperty("name.point.x"));
          p.setProperty("name.point.x", String.valueOf(x - 130));
          System.out.println("after x=" + p.getProperty("name.point.x"));
        }
      }
    }
    TransactionManager.endTransaction();
    pa.saveAs(a[1]);
    pa.close();
  }
}
