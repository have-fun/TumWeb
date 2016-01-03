package havefun.tumweb.injection

import dagger.Component
import havefun.tumweb.Service
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ServerModule::class))
interface ServerComponent {
    fun inject(service: Service)
}