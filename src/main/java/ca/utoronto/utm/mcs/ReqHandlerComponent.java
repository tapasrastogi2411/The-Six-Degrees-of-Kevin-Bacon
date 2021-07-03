package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
// TODO Uncomment The Line Below When You Have Implemented ReqHandlerModule 
@Component(modules = ReqHandlerModule.class)
public interface ReqHandlerComponent {

    public ReqHandler buildHandler();
}
