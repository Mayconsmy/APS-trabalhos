import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
public class Probe15 {
  public static void main(String[] a) throws Exception {
    ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
    pa.open("/tmp/probe14.asta", true, false, true);
    for (IDiagram d : pa.getProject().getDiagrams()) {
      System.out.println("DGM " + d.getName());
      for (IPresentation p : d.getPresentations()) {
        if (p instanceof INodePresentation) {
          INodePresentation n=(INodePresentation)p;
          System.out.println("NODE "+p.getType()+" ["+n.getLabel()+"] "+n.getLocation()+" "+n.getWidth()+"x"+n.getHeight());
          System.out.println("  "+n.getProperties());
        }
      }
    }
    pa.close();
  }
}
