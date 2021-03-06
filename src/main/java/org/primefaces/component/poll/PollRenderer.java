/**
 * Copyright 2009-2019 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.poll;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.WidgetBuilder;

public class PollRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Poll poll = (Poll) component;

        if (context.getExternalContext().getRequestParameterMap().containsKey(poll.getClientId(context))) {
            ActionEvent event = new ActionEvent(poll);
            if (poll.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            poll.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Poll poll = (Poll) component;
        String clientId = poll.getClientId(context);

        AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance(context).getAjaxRequestBuilder();

        String request = builder.init()
                .source(clientId)
                .form(poll, poll)
                .process(component, poll.getProcess())
                .update(component, poll.getUpdate())
                .async(poll.isAsync())
                .global(poll.isGlobal())
                .delay(poll.getDelay())
                .timeout(poll.getTimeout())
                .partialSubmit(poll.isPartialSubmit(), poll.isPartialSubmitSet(), poll.getPartialSubmitFilter())
                .resetValues(poll.isResetValues(), poll.isResetValuesSet())
                .ignoreAutoUpdate(poll.isIgnoreAutoUpdate())
                .onstart(poll.getOnstart())
                .onerror(poll.getOnerror())
                .onsuccess(poll.getOnsuccess())
                .oncomplete(poll.getOncomplete())
                .params(poll)
                .build();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Poll", poll.resolveWidgetVar(), clientId)
                .attr("frequency", poll.getInterval())
                .attr("autoStart", poll.isAutoStart())
                .callback("fn", "function()", request);

        wb.finish();
    }
}
