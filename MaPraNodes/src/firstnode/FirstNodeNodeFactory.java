package firstnode;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "FirstNode" Node.
 * 
 *
 * @author 
 */
public class FirstNodeNodeFactory 
        extends NodeFactory<FirstNodeNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public FirstNodeNodeModel createNodeModel() {
        return new FirstNodeNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<FirstNodeNodeModel> createNodeView(final int viewIndex,
            final FirstNodeNodeModel nodeModel) {
        return new FirstNodeNodeView(nodeModel);
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
        return new FirstNodeNodeDialog();
    }

}

