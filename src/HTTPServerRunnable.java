public class HTTPServerRunnable implements Runnable {
    private String numer;
    private int portNumber;
    private String name;
    private int toSocketPort;

    public HTTPServerRunnable(String numer, String name, int portNumber, int toSocketPort) {
        this.numer = numer;
        this.name = name;
        this.portNumber = portNumber;
        this.toSocketPort = toSocketPort;

    }


    @Override
    public void run() {

        HTTPServer serverHTTP = new HTTPServer(numer, name, portNumber,toSocketPort);
        try {
            serverHTTP.startServerHTTP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
