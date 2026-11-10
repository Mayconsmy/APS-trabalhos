import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.io.File;
import java.util.*;

/** Utility: inspect/export an Astah project without modifying it. */
public class Tools {
    static void p(String s) { System.out.println(s); System.out.flush(); }

    public static void main(String[] args) throws Exception {
        String cmd = args[0];
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.open(args[1], true, false, true);
        if ("inspect".equals(cmd)) inspect(pa.getProject());
        else if ("export".equals(cmd)) export(pa.getProject(), args[2]);
        else if ("dump".equals(cmd)) dump(pa.getProject(), args[2]);
        pa.close();
    }

    static void inspect(IModel project) throws Exception {
        for (IDiagram d : project.getDiagrams()) {
            p("DIAGRAM " + d.getName() + " bound=" + d.getBoundRect());
            for (IPresentation pr : d.getPresentations()) {
                if (pr instanceof INodePresentation) {
                    INodePresentation n = (INodePresentation) pr;
                    p("NODE " + pr.getType() + " label=[" + n.getLabel() + "] loc=" + n.getLocation()
                        + " w=" + n.getWidth() + " h=" + n.getHeight()
                        + " model=" + (n.getModel() == null ? "null" : n.getModel().getClass().getSimpleName())
                        + " id=" + n.getID());
                } else {
                    ILinkPresentation l = (ILinkPresentation) pr;
                    p("LINK " + pr.getType() + " label=[" + l.getLabel() + "]"
                        + " src=" + (l.getSource() == null ? "null" : l.getSource().getLabel())
                        + " tgt=" + (l.getTarget() == null ? "null" : l.getTarget().getLabel())
                        + " model=" + (l.getModel() == null ? "null" : l.getModel().getClass().getSimpleName())
                        + " id=" + l.getID() + " pts=" + Arrays.toString(l.getAllPoints()));
                }
            }
        }
    }

    static void dump(IModel project, String only) throws Exception {
        for (IDiagram d : project.getDiagrams()) {
            if (only != null && !d.getName().contains(only)) continue;
            p("=== " + d.getName() + " ===");
            for (IPresentation pr : d.getPresentations()) {
                if (!(pr instanceof ILinkPresentation)) continue;
                ILinkPresentation l = (ILinkPresentation) pr;
                p("LINK " + pr.getType() + " [" + l.getLabel() + "] model=" + (l.getModel() == null ? "null" : l.getModel().getClass().getSimpleName()));
                if (l.getModel() instanceof IMessage) {
                    IMessage m = (IMessage) l.getModel();
                    p("   msg src=" + (m.getSource() == null ? "null" : m.getSource().getName())
                        + " tgt=" + (m.getTarget() == null ? "null" : m.getTarget().getName())
                        + " return=" + m.isReturnMessage() + " create=" + m.isCreateMessage() + " destroy=" + m.isDestroyMessage()
                        + " arg=" + m.getArgument() + " act=" + (m.getActivator() == null ? "null" : m.getActivator().getName()));
                } else if (l.getModel() instanceof IDependency) {
                    IDependency dep = (IDependency) l.getModel();
                    p("   dep client=" + dep.getClient().getName() + " supplier=" + dep.getSupplier().getName()
                        + " stereo=" + Arrays.toString(dep.getStereotypes()));
                } else if (l.getModel() instanceof IAssociation) {
                    IAssociation a = (IAssociation) l.getModel();
                    p("   assoc name=" + a.getName() + " stereo=" + Arrays.toString(a.getStereotypes()));
                    for (IAttribute e : a.getMemberEnds()) {
                        p("      end name=" + e.getName() + " type=" + (e.getType() == null ? "null" : e.getType().getName())
                            + " mult=" + e.getMultiplicityString() + " nav=" + e.isNavigable() + " agg=" + e.isAggregation()
                            + " comp=" + e.isComposite() + " vis=" + e.getVisibility());
                    }
                }
                p("   pts=" + Arrays.toString(l.getAllPoints()));
            }
        }
    }

    static void export(IModel project, String dir) throws Exception {
        new File(dir).mkdirs();
        int i = 0;
        for (IDiagram d : project.getDiagrams()) {
            i++;
            String name = d.getName();
            String num = name.replaceAll("^Questao\\s+(\\d+).*$", "$1");
            String file = String.format("%s/q%02d.png", dir, Integer.parseInt(num));
            d.exportImage(file, "png", 3.0);
            p("EXPORTED " + file);
        }
    }
}
