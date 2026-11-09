import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.*;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import java.awt.geom.Point2D;

public class Probe14 {
    static void p(String s) { System.out.println(s); System.out.flush(); }
    static IOperation op(BasicModelEditor b, IClass c, String n, String r, String v) throws Exception {
        IOperation o = b.createOperation(c, n, r); o.setVisibility(v); return o;
    }
    static IAttribute at(BasicModelEditor b, IClass c, String n, String t, String v) throws Exception {
        IAttribute a = b.createAttribute(c, n, t); a.setVisibility(v); return a;
    }
    public static void main(String[] args) throws Exception {
      try {
        ProjectAccessor pa = AstahAPI.getAstahAPI().getProjectAccessor();
        pa.create("/tmp/probe14.asta");
        IModel project = pa.getProject();
        BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
        TransactionManager.beginTransaction();
        bme.setLanguageJava(project, true);
        IPackage pkg = bme.createPackage(project, "Q07");
        IClass logavel = bme.createInterface(pkg, "Logavel");
        op(bme, logavel, "log", "", INamedElement.PUBLIC_VISIBILITY);
        IClass arq = bme.createClass(pkg, "LogEmArquivo");
        op(bme, arq, "log", "", INamedElement.PUBLIC_VISIBILITY);
        IClass db = bme.createClass(pkg, "LogEmBancoDeDados");
        op(bme, db, "log", "", INamedElement.PUBLIC_VISIBILITY);
        IClass serv = bme.createClass(pkg, "ServicoDeAplicacao");
        at(bme, serv, "logavel", "Logavel", INamedElement.PRIVATE_VISIBILITY);
        IRealization r1 = bme.createRealization(arq, logavel, "");
        IRealization r2 = bme.createRealization(db, logavel, "");
        IDependency dep = bme.createDependency(serv, logavel, "usa");
        ClassDiagramEditor de = pa.getDiagramEditorFactory().getClassDiagramEditor();
        IClassDiagram dgm = de.createClassDiagram(project, "Q07 Teste");
        INodePresentation ln = de.createNodePresentation(logavel, new Point2D.Double(500,200));
        INodePresentation an = de.createNodePresentation(arq, new Point2D.Double(300,500));
        INodePresentation dn = de.createNodePresentation(db, new Point2D.Double(700,500));
        INodePresentation sn = de.createNodePresentation(serv, new Point2D.Double(80,80));
        de.createLinkPresentation(r1, ln, an);
        de.createLinkPresentation(r2, ln, dn);
        de.createLinkPresentation(dep, sn, ln);
        INodePresentation note = de.createNote("Dependência via atributo:\nprivate Logavel logavel", new Point2D.Double(850,80));
        de.createNoteAnchor(note, sn);
        // Q08 assoc/aggregation
        IPackage pkg8 = bme.createPackage(project, "Q08");
        IClass comp = bme.createClass(pkg8, "CompressorDeArquivos");
        IClass alg = bme.createInterface(pkg8, "AlgoritmoDeCompressao");
        at(bme, comp, "algoritmo", "AlgoritmoDeCompressao", INamedElement.PRIVATE_VISIBILITY);
        op(bme, alg, "comprimir", "", INamedElement.PUBLIC_VISIBILITY);
        IAssociation assoc = bme.createAssociation(comp, alg, "usa", "1", "1");
        IClassDiagram dgm8 = de.createClassDiagram(project, "Q08 Teste");
        INodePresentation cn8 = de.createNodePresentation(comp, new Point2D.Double(100,250));
        INodePresentation an8 = de.createNodePresentation(alg, new Point2D.Double(520,60));
        ILinkPresentation alink = de.createLinkPresentation(assoc, cn8, an8);
        p("assoc ends: " + assoc.getMemberEnds()[0].getName() + " / " + assoc.getMemberEnds()[1].getName());
        p("attrs comp: " + java.util.Arrays.toString(comp.getAttributes()));
        // Aggregation Q09
        IPackage pkg9 = bme.createPackage(project, "Q09");
        IClass prod = bme.createClass(pkg9, "ProdutoLeilao");
        IClass inte = bme.createInterface(pkg9, "Interessado");
        at(bme, prod, "interessados", "List<Interessado>", INamedElement.PRIVATE_VISIBILITY);
        op(bme, inte, "atualizar", "", INamedElement.PUBLIC_VISIBILITY);
        IAssociation agg = bme.createAssociation(prod, inte, "observa", "1", "0..*");
        agg.getMemberEnds()[0].setAggregation();
        IClassDiagram dgm9 = de.createClassDiagram(project, "Q09 Teste");
        INodePresentation pn9 = de.createNodePresentation(prod, new Point2D.Double(100,150));
        INodePresentation in9 = de.createNodePresentation(inte, new Point2D.Double(550,150));
        de.createLinkPresentation(agg, pn9, in9);
        for (IDiagram d : new IDiagram[]{dgm,dgm8,dgm9}) for (IPresentation pr : d.getPresentations()) if ("Frame".equals(pr.getType())) pr.setProperty("visibility","false");
        TransactionManager.endTransaction();
        pa.save(); p("SAVED"); pa.close();
      } catch (Throwable t) { t.printStackTrace(); System.out.flush(); System.exit(2); }
    }
}
