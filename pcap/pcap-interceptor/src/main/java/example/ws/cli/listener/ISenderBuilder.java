package example.ws.cli.listener;


import example.ws.cli.IConnectionRegistrator;

public interface ISenderBuilder {
    IConnectionRegistrator buildConnectionRegistrator();
}
