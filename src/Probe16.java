import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;

public class Probe16 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    static IOperation op(BasicModelEditor b, IClass c, String n, String r, String v) throws Exception {
        IOperation o = b.createOperation(c, n, r); o.setVisibility(v); return o;
    }
    static IAttribute at(BasicModelEditor b, IClass c, String n, String t, String v) throws Exception {
        IAttribute a = b.createAttribute(c, n, t); a.setVisibility(v); return a;
    }
    static void normal(IPresentation p) throws Exception { p.setProperty("notation_type","normal"); }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe16.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        bme.setLanguageJava(project, true);
        IPackage pkg = bme.createPackage(project, "Q08");
        IClass alg = bme.createInterface(pkg, "AlgoritmoDeCompressao");
        op(bme, alg, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        IClass comp = bme.createClass(pkg, "CompressorDeArquivos");
        at(bme, comp, "algoritmo", "AlgoritmoDeCompressao", INamedElement.PRIVATE_VISIBILITY);
        IAssociation assoc = bme.createAssociation(comp, alg, "usa", "", "");
        assoc.getMemberEnds()[0].setMultiplicityString("1");
        assoc.getMemberEnds()[1].setMultiplicityString("1");
        IClass zip = bme.createClass(pkg, "CompressaoZIP");
        op(bme, zip, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        IRealization real = bme.createRealization(zip, alg, "");
        ClassDiagramEditor de = pa.getDiagramEditorFactory().getClassDiagramEditor();
        IClassDiagram dgm = de.createClassDiagram(project, "Q08 Teste2");
        INodePresentation cn = de.createNodePresentation(comp, new Point2D.Double(100,250));
        INodePresentation an = de.createNodePresentation(alg, new Point2D.Double(520,60));
        INodePresentation zn = de.createNodePresentation(zip, new Point2D.Double(500,400));
        normal(an);
        ILinkPresentation al = de.createLinkPresentation(assoc, cn, an);
        de.createLinkPresentation(real, an, zn);
        p("assoc ends: [" + assoc.getMemberEnds()[0].getName() + "] [" + assoc.getMemberEnds()[1].getName() + "]");
        p("comp attrs " + java.util.Arrays.toString(comp.getAttributes()));
        p("alg attrs " + java.util.Arrays.toString(alg.getAttributes()));
        for (IPresentation pr : dgm.getPresentations()) if ("Frame".equals(pr.getType())) pr.setProperty("visibility","false");
        TransactionManager.endTransaction();
        pa.save(); p("SAVED"); pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
