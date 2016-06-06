package metabotogeno.node;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "MetaboToGeno" Node.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class MetaboToGenoNodeView extends NodeView<MetaboToGenoNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link MetaboToGenoNodeModel})
     */
    protected MetaboToGenoNodeView(final MetaboToGenoNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        MetaboToGenoNodeModel nodeModel = 
            (MetaboToGenoNodeModel)getNodeModel();
        assert nodeModel != null;        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
    }

}

