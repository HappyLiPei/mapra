package geneticnetwork.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "GeneticNetworkScore" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class GeneticNetworkScoreNodeFactory 
        extends NodeFactory<GeneticNetworkScoreNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneticNetworkScoreNodeModel createNodeModel() {
        return new GeneticNetworkScoreNodeModel();
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
    public NodeView<GeneticNetworkScoreNodeModel> createNodeView(final int viewIndex,
            final GeneticNetworkScoreNodeModel nodeModel) {
        return new GeneticNetworkScoreNodeView(nodeModel);
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
        return new GeneticNetworkScoreNodeDialog();
    }

}

