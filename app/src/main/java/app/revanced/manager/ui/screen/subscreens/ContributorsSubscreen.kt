package app.revanced.manager.ui.screen.subscreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.ContributorsCard
import app.revanced.manager.ui.navigation.AppDestination
import app.revanced.manager.ui.viewmodel.ContributorsViewModel
import app.revanced.manager.util.ghOrganization
import app.revanced.manager.util.openUrl
import com.xinto.taxi.BackstackNavigator
import org.koin.androidx.compose.getViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ContributorsSubscreen(
    navigator: BackstackNavigator<AppDestination>,
    vm: ContributorsViewModel = getViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val ctx = LocalContext.current.applicationContext
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_contributors_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { ctx.openUrl(ghOrganization) }) {
                Icon(painterResource(id = R.drawable.ic_github), contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(bottom = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ContributorsCard(
                stringResource(R.string.cli_contributors),
                data = vm.cliContributorsList
            )
            ContributorsCard(
                stringResource(R.string.patcher_contributors),
                data = vm.patcherContributorsList
            )
            ContributorsCard(
                stringResource(R.string.patches_contributors),
                data = vm.patchesContributorsList
            )
            ContributorsCard(
                stringResource(R.string.manager_contributors),
                data = vm.managerContributorsList
            )
            ContributorsCard(
                stringResource(R.string.integrations_contributors),
                data = vm.integrationsContributorsList
            )
        }
    }
}