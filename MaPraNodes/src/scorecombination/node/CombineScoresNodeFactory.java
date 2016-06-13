package scorecombination.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "CombineScores" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class CombineScoresNodeFactory 
        extends NodeFactory<CombineScoresNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CombineScoresNodeModel createNodeModel() {
        return new CombineScoresNodeModel(3);
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
    public NodeView<CombineScoresNodeModel> createNodeView(final int viewIndex,
            final CombineScoresNodeModel nodeModel) {
        return new CombineScoresNodeView(nodeModel);
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
        return new CombineScoresNodeDialog();
    }

}

