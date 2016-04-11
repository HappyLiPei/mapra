package metabolites.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ScoreMetabolites" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class ScoreMetabolitesNodeFactory 
        extends NodeFactory<ScoreMetabolitesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreMetabolitesNodeModel createNodeModel() {
        return new ScoreMetabolitesNodeModel();
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
    public NodeView<ScoreMetabolitesNodeModel> createNodeView(final int viewIndex,
            final ScoreMetabolitesNodeModel nodeModel) {
        return new ScoreMetabolitesNodeView(nodeModel);
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
        return new ScoreMetabolitesNodeDialog();
    }

}

