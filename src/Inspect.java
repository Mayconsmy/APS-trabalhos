import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class Inspect {
    public static void main(String[] args) throws Exception {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.open(args[0], true, false, true);
        IModel project = pa.getProject();
        for (IDiagram d : project.getDiagrams()) {
            System.out.println("DIAGRAM " + d.getName() + " bound=" + d.getBoundRect());
            for (IPresentation p : d.getPresentations()) {
                if (p instanceof INodePresentation) {
                    INodePresentation n = (INodePresentation) p;
                    System.out.println("NODE " + p.getType() + " label=[" + n.getLabel() + "] loc=" + n.getLocation() + " w=" + n.getWidth() + " h=" + n.getHeight() + " model=" + (n.getModel()==null?"null":n.getModel().getClass().getSimpleName()));
                    System.out.println("   props=" + n.getProperties());
                } else {
                    System.out.println("LINK " + p.getType() + " label=[" + p.getLabel() + "]");
                }
            }
        }
        pa.close();
    }
}
