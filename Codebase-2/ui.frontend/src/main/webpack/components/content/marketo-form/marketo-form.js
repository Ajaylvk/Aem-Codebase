import AEM from '../../../core/js/AemComponent';

/*eslint-disable */
class MarketoForm extends AEM.Component {

    constructor(element) {
        super(element);
        this.name = 'Marketo form';
        this.digitalDataTrackable = true;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
       /* var digitalData = digitalData || {};
        MktoForms2.whenReady((form) => {
            const formId = form.getId();

            window.digitalData.events = window.digitalData.events || [];

            window.digitalData.events.push({
                eventInfo: {
                    eventType: 'formLoad',
                    eventName: formId,
                    formName: 'formName',
                },
            });

            form.onSubmit((form) => {
                const formId = form.getId();
                window.digitalData = window.digitalData || {};
                window.digitalData.events = window.digitalData.events || [];
                window.digitalData.events.push({
                    eventInfo: {
                        eventType: 'formSubmit',
                        eventName: formId,
                        formName: 'formName',
                    },
                });
            });
        });*/
    }
}

AEM.registerComponent('.marketoform', MarketoForm);
