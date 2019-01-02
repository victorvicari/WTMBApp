package com.wtmberlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.wtmberlin.data.accessToken

class MainActivity : AppCompatActivity(), ManageFragments, EventsFragment.OnEventChosenListener {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initNavigationDrawer()
        initEventsFragment()

        if (accessToken == null) {
            //authorizeUser()
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        initActionBar()
    }

    private fun initActionBar() {
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun initNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.menu_item_social_media -> displaySocialMedia()
                R.id.menu_item_wtm_berlin -> displayInfo()
                R.id.menu_item_wtm -> Toast.makeText(this, "will be implemented soon", Toast.LENGTH_LONG).show()
                R.id.menu_item_wtm_stats -> displayStats()
                R.id.menu_item_wtm_events -> displayEvents()
            }
            true
        }
    }

    private fun initEventsFragment() {
        val eventsFragment: EventsFragment = EventsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, eventsFragment)
            .addToBackStack(null)
            .commit()
        eventsFragment.setOnEventChosenListener(this)
    }

    private fun authorizeUser() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "https://secure.meetup.com/oauth2/authorize?client_id=8h22npmn9nfg58mco97blumdg9&response_type=code&redirect_uri=http%3A%2F%2Fwtmberlin.com%2Fandroid-app"
            )
        )
        startActivity(browserIntent)
    }

    override fun displayEventDetails() {
        Toast.makeText(this,"Event details will be implemented soon", Toast.LENGTH_LONG).show()
    }

    override fun displayEvents() {
        initEventsFragment()
    }

    override fun displayStats() {
        Toast.makeText(this,"Stats will be implemented soon", Toast.LENGTH_LONG).show()
    }

    override fun displayInfo() {
        val infoFragment = CommunityInfoFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, infoFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun displaySocialMedia() {
        val socialMediaFragment = SocialMediaFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, socialMediaFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

