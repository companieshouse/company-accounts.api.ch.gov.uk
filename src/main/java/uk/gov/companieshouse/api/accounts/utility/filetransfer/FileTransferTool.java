package uk.gov.companieshouse.api.accounts.utility.filetransfer;

public interface FileTransferTool {

    /**
     * It will download a file from the location passed in.
     *
     * @param fileLocation - Contains the public location of the file.
     * @return {@link String} containing the downloaded file. Return null if file not downloaded.
     */
    String downloadFileFromLocation(String fileLocation);
}
