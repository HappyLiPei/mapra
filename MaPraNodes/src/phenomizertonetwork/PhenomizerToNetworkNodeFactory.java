package phenomizertonetwork;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PhenomizerToNetwork" Node.
 * 
 *
 * @author 
 */
public class PhenomizerToNetworkNodeFactory 
        extends NodeFactory<PhenomizerToNetworkNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenomizerToNetworkNodeModel createNodeModel() {
        return new PhenomizerToNetworkNodeModel();
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
    public NodeView<PhenomizerToNetworkNodeModel> createNodeView(final int viewIndex,
            final PhenomizerToNetworkNodeModel nodeModel) {
        return new PhenomizerToNetworkNodeView(nodeModel);
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
        return new PhenomizerToNetworkNodeDialog();
    }

}

