package phenomizer.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Phenomizer" Node.
 * 
 *
 * @author 
 */
public class PhenomizerNodeFactory 
        extends NodeFactory<PhenomizerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenomizerNodeModel createNodeModel() {
        return new PhenomizerNodeModel();
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
    public NodeView<PhenomizerNodeModel> createNodeView(final int viewIndex,
            final PhenomizerNodeModel nodeModel) {
        return new PhenomizerNodeView(nodeModel);
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
        return new PhenomizerNodeDialog();
    }

}

