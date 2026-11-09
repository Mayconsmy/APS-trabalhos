import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;

public class Probe17 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    static IOperation op(BasicModelEditor b, IClass c, String n, String r, String v) throws Exception {
        IOperation o = b.createOperation(c, n, r); o.setVisibility(v); return o;
    }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe17.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        bme.setLanguageJava(project, true);
        IPackage pkg = bme.createPackage(project, "Q09");
        IClass inte = bme.createInterface(pkg, "Interessado");
        op(bme, inte, "atualizar", "", INamedElement.PUBLIC_VISIBILITY);
        IClass prod = bme.createClass(pkg, "ProdutoLeilao");
        IAttribute ai = bme.createAttribute(prod, "interessados", "List<Interessado>");
        ai.setVisibility(INamedElement.PRIVATE_VISIBILITY);
        IClass comp = bme.createClass(pkg, "Comprador");
        op(bme, comp, "atualizar", "", INamedElement.PUBLIC_VISIBILITY);
        IAssociation agg = bme.createAssociation(prod, inte, "observa", "", "");
        agg.getMemberEnds()[1].setMultiplicityString("0..*");
        agg.getMemberEnds()[0].setAggregation();
        IRealization real = bme.createRealization(comp, inte, "");
        IDependency dep = bme.createDependency(comp, prod, "se registra");
        ClassDiagramEditor de = pa.getDiagramEditorFactory().getClassDiagramEditor();
        IClassDiagram dgm = de.createClassDiagram(project, "Q09 Teste2");
        INodePresentation pn = de.createNodePresentation(prod, new Point2D.Double(100,100));
        INodePresentation inn = de.createNodePresentation(inte, new Point2D.Double(600,100));
        INodePresentation cn = de.createNodePresentation(comp, new Point2D.Double(800,400));
        inn.setProperty("notation_type","normal");
        de.createLinkPresentation(agg, pn, inn);
        de.createLinkPresentation(real, inn, cn);
        de.createLinkPresentation(dep, cn, pn);
        for (IPresentation pr : dgm.getPresentations()) if ("Frame".equals(pr.getType())) pr.setProperty("visibility","false");
        TransactionManager.endTransaction();
        pa.save(); p("SAVED"); pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
