package metabotogeno.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MetaboToGeno" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class MetaboToGenoNodeFactory 
        extends NodeFactory<MetaboToGenoNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaboToGenoNodeModel createNodeModel() {
        return new MetaboToGenoNodeModel();
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
    public NodeView<MetaboToGenoNodeModel> createNodeView(final int viewIndex,
            final MetaboToGenoNodeModel nodeModel) {
        return new MetaboToGenoNodeView(nodeModel);
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
        return new MetaboToGenoNodeDialog();
    }

}

