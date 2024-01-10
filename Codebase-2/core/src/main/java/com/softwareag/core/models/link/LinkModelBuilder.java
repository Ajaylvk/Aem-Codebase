package com.softwareag.core.models.link;



import java.util.function.Consumer;


public final class LinkModelBuilder {
    private String text;
    private String href;
    private String target;

    public LinkModelBuilder with(Consumer<LinkModelBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }

    public LinkModel createLinkModel() {
        LinkModel linkModel = new LinkModel();
        linkModel.setText(this.text);
        linkModel.setRawHref(this.href);
        linkModel.setTarget(this.target);
        linkModel.init();
        return linkModel;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
