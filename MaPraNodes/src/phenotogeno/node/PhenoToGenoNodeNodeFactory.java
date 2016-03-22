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
public class PhenoToGenoNodeNodeFactory 
        extends NodeFactory<PhenoToGenoNodeNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenoToGenoNodeNodeModel createNodeModel() {
        return new PhenoToGenoNodeNodeModel();
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
    public NodeView<PhenoToGenoNodeNodeModel> createNodeView(final int viewIndex,
            final PhenoToGenoNodeNodeModel nodeModel) {
        return new PhenoToGenoNodeNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new PhenoToGenoNodeNodeDialog();
    }

}

