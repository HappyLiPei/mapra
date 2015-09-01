package phenomizertoclustering;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PhenomizerToClustering" Node.
 * 
 *
 * @author 
 */
public class PhenomizerToClusteringNodeFactory 
        extends NodeFactory<PhenomizerToClusteringNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenomizerToClusteringNodeModel createNodeModel() {
        return new PhenomizerToClusteringNodeModel();
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
    public NodeView<PhenomizerToClusteringNodeModel> createNodeView(final int viewIndex,
            final PhenomizerToClusteringNodeModel nodeModel) {
        return new PhenomizerToClusteringNodeView(nodeModel);
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
        return new PhenomizerToClusteringNodeDialog();
    }

}

