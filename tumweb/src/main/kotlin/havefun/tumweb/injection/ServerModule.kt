package havefun.tumweb.injection

import dagger.Module
import dagger.Provides
import havefun.tumweb.Host
import havefun.tumweb.host.NanoHttpServer
import javax.inject.Singleton

@Module
class ServerModule(private val portNo: Int) {
    @Provides
    @Singleton
    fun provideHost(): Host = NanoHttpServer(portNo)
}