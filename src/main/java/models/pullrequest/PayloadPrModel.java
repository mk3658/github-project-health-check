package models.pullrequest;

import models.pullrequest.PrModel;

/**
 * @author Quan Kien Minh
 *
 */
public class PayloadPrModel {
    private PrModel payload;

    public PayloadPrModel(PrModel payload) {
        this.payload = payload;
    }

    public PrModel getPayload() {
        return payload;
    }
}

