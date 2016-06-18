package phenotogeno.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PhenoToGenoNode" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeFactory 
        extends NodeFactory<PhenoToGenoNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenoToGenoNodeModel createNodeModel() {
        return new PhenoToGenoNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<PhenoToGenoNodeModel> createNodeView(final int viewIndex,
            final PhenoToGenoNodeModel nodeModel) {
        return new PhenoToGenoNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new PhenoToGenoNodeDialog();
    }

}

