import AEM from '../../../../core/js/AemComponent';
import wait from '../../../../core/js/utils/wait';

class AccordionItem extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Accordion Item';
        this.digitalDataTrackable = true;

        this._animationTime = 300; // [in milliseconds] keep in sync with css animation-time
        this.selectors = {
            button: '.accordion-item__button',
            contentContainer: '.accordion-item__content-container',
            content: '.accordion-item__content',
        };
        this.states = {
            open: 'accordion-item--open',
            closing: 'accordion-item--closing',
        };
        this.$accordionItemButton = null;
        this.$accordionItemContentContainer = null;
    }

    init() {
        this.$accordionItemButton = $(this.element).find(this.selectors.button);
        this.$accordionItemContentContainer = $(this.element).find(this.selectors.contentContainer);
        this.$accordionItemContent = $(this.element).find(this.selectors.content);
        this.$accordionItemButton.on('click', () => this._toggle());

        const $video = this.$accordionItemContentContainer.find('.media [data-viewer-type="VideoViewer"]');
        if ($video.length) {
            this.$accordionItemContentContainer.show();
            const observer = new MutationObserver(
                mutationsList => this._videoLoaded(mutationsList)
            );
            observer.observe($video.get(0), { attributes: true, childList: true, subtree: true });
        }

        const $image = this.$accordionItemContentContainer.find('.media [data-asset-type="image"]');
        if ($image.length) {
            this.$accordionItemContentContainer.show();
            const observer = new MutationObserver(
                mutationsList => this._imageLoaded(mutationsList)
            );
            observer.observe($image.get(0), { attributes: true, childList: true, subtree: true });
        }

        if (!$video.length && !$image.length) {
            this._scrollToAnchor();
        }

        window.addEventListener('hashchange', () => {
            this._scrollToAnchor();
        });
    }

    _videoLoaded(mutationsList) {
        // loaded when s7container has height and position style set
        const isS7containerLoaded = mutationsList.filter(mutation => mutation.target.classList.contains('s7container') && mutation.target.style.length === 2);
        if (isS7containerLoaded.length) {
            this._scrollToAnchor();
        }
    }

    _imageLoaded(mutationsList) {
        const childNodeAdded = mutationsList.filter(mutation => mutation.type === 'attributes' && mutation.attributeName === 'src');
        if (childNodeAdded.length) {
            this._scrollToAnchor();
        }
    }

    _scrollToAnchor() {
        const anchor = window.location.hash.substr(1);
        const nodeToScrollTo = $(this.element).find(`[id="${anchor}"]`);
        if (nodeToScrollTo.length) {
            this._setHeightOnItemContent(this._getHeight());
            this.element.classList.add(this.states.open);
            const offsetTop = nodeToScrollTo.offset().top;
            this._setHeightOnItemContent('0px');
            $('html, body').animate({
                scrollTop: offsetTop,
            }, 500, () => this._open());
            this.element.classList.remove(this.states.open);
        }
    }

    _toggle() {
        if (!$(this.element).hasClass(this.states.open)) {
            this._open();
        } else {
            this._close();
        }
    }

    _open() {
        this._setHeightOnItemContent(this._getHeight());
        this.element.classList.add(this.states.open);
    }

    _close() {
        this.element.classList.add(this.states.closing);
        this._setHeightOnItemContent('0px');
        wait(this._animationTime).then(() => {
            this.element.classList.remove(this.states.open, this.states.closing);
        });
    }

    _setHeightOnItemContent(height) {
        if (this.$accordionItemContentContainer.length > 0) {
            this.$accordionItemContentContainer.get(0).setAttribute('style', `height: ${height};`);
        }
    }

    _getHeight() {
        this.$accordionItemContentContainer.get(0).setAttribute('style', 'display: block;');
        const height = `${this.$accordionItemContentContainer.get(0).scrollHeight}px`;
        this.$accordionItemContentContainer.get(0).setAttribute('style', 'display: none;');
        return height;
    }
}

AEM.registerComponent('.accordion-item', AccordionItem);
