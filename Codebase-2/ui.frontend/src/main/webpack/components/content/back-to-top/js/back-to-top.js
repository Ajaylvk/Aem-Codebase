import AEM from '../../../../core/js/AemComponent';
import debounce from '../../../../core/js/utils/debounce';

class BackToTop extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.$backToTop = undefined;
        this.$document = undefined;
    }

    init() {
        this.$backToTop = $(this.element).parent();
        this.$document = $(document.documentElement);
        this.$backToTop.on('click', () => this.handleClick());

        const handleScroll = debounce(() => {
            this.toggleVisibility();
        }, 66);

        window.addEventListener('scroll', handleScroll);

        // set initial visibility
        this.toggleVisibility();
    }

    toggleVisibility() {
        if (window.pageYOffset > 0) {
            this.$backToTop.fadeIn();
        } else {
            this.$backToTop.fadeOut();
        }
    }

    handleClick() {
        this.$document.stop().animate({ scrollTop: 0 }, '500');
    }
}

AEM.registerComponent('.back-to-top', BackToTop);
