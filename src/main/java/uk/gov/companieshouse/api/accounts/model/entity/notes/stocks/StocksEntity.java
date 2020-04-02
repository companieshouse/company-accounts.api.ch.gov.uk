package uk.gov.companieshouse.api.accounts.model.entity.notes.stocks;

import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

@Document(collection = "notes")
public class StocksEntity extends NoteEntity {

    private StocksDataEntity data;

    public StocksDataEntity getData() {
        return data;
    }

    public void setData(StocksDataEntity data) {
        this.data = data;
    }
}
