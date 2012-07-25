package org.motechproject.whp.ivr.builder;


import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.whp.adherence.domain.AdherenceSummaryByProvider;
import org.motechproject.whp.ivr.WHPIVRMessage;

import static org.motechproject.whp.ivr.IvrAudioFiles.*;

public class AdherenceSummaryPrompts {

    public static Prompt[] adherenceSummary(WHPIVRMessage whpivrMessage, AdherenceSummaryByProvider adherenceSummaryByProvider) {
        PromptBuilder promptBuilder = new PromptBuilder(whpivrMessage);

        promptBuilder.wav(ADHERENCE_PROVIDED_FOR)
                .number(adherenceSummaryByProvider.countOfPatientsWithAdherence())
                .wav(ADHERENCE_TO_BE_PROVIDED_FOR)
                .number(adherenceSummaryByProvider.countOfPatientsWithoutAdherence())
                .wav(ADHERENCE_CAPTURE_INSTRUCTION);
        return promptBuilder.build();
    }
}

