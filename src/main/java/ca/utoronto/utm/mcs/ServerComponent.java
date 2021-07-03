package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
// TODO Uncomment The Line Below When You Have Implemented ServerModule 
@Component(modules = ServerModule.class)
public interface ServerComponent {

	public Server buildServer();
}
