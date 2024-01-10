import PubSub from '../../../../core/js/utils/pubsub';
import AEM from '../../../../core/js/AemComponent';

class Cta extends AEM.Component {

    constructor(element) {
        super(element);
        this.$el = element;
        this.name = 'CTA';
    }

    init() {
        this.__bindEvents();
    }

    __bindEvents() {
        // bind click event to "a-cta" class of button
        $(this.$el).on('click', (e) => {
            const href = $(e.currentTarget).attr('href');
            // If no href define then exit function
            if (!href) return;

            const url = new URL(href);
            if (url.origin === 'null' && url.hash !== '') {
                PubSub.publish('scroll-to-tab', url.hash);
            }
        });
    }
}

AEM.registerComponent('.a-cta', Cta);
