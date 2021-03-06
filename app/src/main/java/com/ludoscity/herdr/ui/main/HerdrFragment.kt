/*
 *     Copyright (c) 2020. f8full https://github.com/f8full
 *     Herdr is a privacy conscious multiplatform mobile data collector
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ludoscity.herdr.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ludoscity.herdr.BR
import com.ludoscity.herdr.R
import com.ludoscity.herdr.common.data.repository.UserActivityTrackingRepository
import com.ludoscity.herdr.common.ui.main.HerdrFragmentViewModel
import com.ludoscity.herdr.databinding.FragmentHerdrBinding
import com.ludoscity.herdr.utils.setSvgColor
import dev.icerock.moko.mvvm.MvvmEventsFragment
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.mvvm.dispatcher.eventsDispatcherOnMain
import kotlin.time.ExperimentalTime

@ExperimentalTime
class HerdrFragment : MvvmEventsFragment<FragmentHerdrBinding, HerdrFragmentViewModel,
        HerdrFragmentViewModel.HerdrFragmentEventListener>(), HerdrFragmentViewModel.HerdrFragmentEventListener {
    override val layoutId: Int = R.layout.fragment_herdr
    override val viewModelVariableId: Int = BR.herdrFragmentViewModel
    override val viewModelClass: Class<HerdrFragmentViewModel> = HerdrFragmentViewModel::class.java

    override fun viewModelFactory(): ViewModelProvider.Factory {
        return createViewModelFactory { HerdrFragmentViewModel(eventsDispatcherOnMain()) }
    }

    override fun routeToDriveSettings(folderId: String) {
        val bundle = bundleOf(
            "folderId" to folderId,
            "folderName" to binding.folderNameText.text,
            "stackUrl" to binding.driveUrlText.text
        )
        this.findNavController().navigate(R.id.action_herdrFragment_to_driveEditFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreateView(inflater, container, savedInstanceState)

        viewModel.addUserActivityObserver { newUserActivity ->

            binding.apply {
                // reset icon colors
                stillImageView.setSvgColor(R.color.black)
                walkImageView.setSvgColor(R.color.black)
                runImageView.setSvgColor(R.color.black)
                bikeImageView.setSvgColor(R.color.black)
                vehicleImageView.setSvgColor(R.color.black)

                // show all times
                lastWalkText.visibility = View.INVISIBLE
                lastRunText.visibility = View.INVISIBLE
                lastBikeText.visibility = View.INVISIBLE
                lastVehicleText.visibility = View.INVISIBLE

                // reset all switches
                recWalkSwitch.isEnabled = true
                recRunSwitch.isEnabled = true
                recBikeSwitch.isEnabled = true
                recVehicleSwitch.isEnabled = true

                // reset REC icon visibility
                walkRecIcon.visibility = View.INVISIBLE
                runRecIcon.visibility = View.INVISIBLE
                bikeRecIcon.visibility = View.INVISIBLE
                vehicleRecIcon.visibility = View.INVISIBLE
                walkRecIconDisabled.visibility = View.INVISIBLE
                runRecIconDisabled.visibility = View.INVISIBLE
                bikeRecIconDisabled.visibility = View.INVISIBLE
                vehicleRecIconDisabled.visibility = View.INVISIBLE


                when (newUserActivity) {
                    null -> {
                        lastWalkText.visibility = View.VISIBLE
                        lastRunText.visibility = View.VISIBLE
                        lastBikeText.visibility = View.VISIBLE
                        lastVehicleText.visibility = View.VISIBLE
                    }
                    UserActivityTrackingRepository.UserActivity.STILL -> {
                        stillImageView.setSvgColor(R.color.theme_accent)
                        lastWalkText.visibility = View.VISIBLE
                        lastRunText.visibility = View.VISIBLE
                        lastBikeText.visibility = View.VISIBLE
                        lastVehicleText.visibility = View.VISIBLE
                    }

                    UserActivityTrackingRepository.UserActivity.WALK -> {
                        walkImageView.setSvgColor(R.color.theme_accent)
                        recWalkSwitch.isEnabled = false
                        lastWalkText.visibility = View.VISIBLE
                        if (recWalkSwitch.isChecked) {
                            walkRecIcon.visibility = View.VISIBLE
                        } else {
                            walkRecIconDisabled.visibility = View.VISIBLE
                        }
                    }
                    UserActivityTrackingRepository.UserActivity.RUN -> {
                        runImageView.setSvgColor(R.color.theme_accent)
                        recRunSwitch.isEnabled = false
                        lastRunText.visibility = View.VISIBLE
                        if (recRunSwitch.isChecked) {
                            runRecIcon.visibility = View.VISIBLE
                        } else {
                            runRecIconDisabled.visibility = View.VISIBLE
                        }
                    }
                    UserActivityTrackingRepository.UserActivity.BIKE -> {
                        bikeImageView.setSvgColor(R.color.theme_accent)
                        recBikeSwitch.isEnabled = false
                        lastBikeText.visibility = View.VISIBLE
                        if (recBikeSwitch.isChecked) {
                            bikeRecIcon.visibility = View.VISIBLE
                        } else {
                            bikeRecIconDisabled.visibility = View.VISIBLE
                        }
                    }
                    UserActivityTrackingRepository.UserActivity.VEHICLE -> {
                        vehicleImageView.setSvgColor(R.color.theme_accent)
                        recVehicleSwitch.isEnabled = false
                        lastVehicleText.visibility = View.VISIBLE
                        if (recVehicleSwitch.isChecked) {
                            vehicleRecIcon.visibility = View.VISIBLE
                        } else {
                            vehicleRecIconDisabled.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewModel.apply {
            stackBaseUrlText.addObserver { url ->
                binding.driveUrlText.text = url
            }

            cloudDirectoryName.addObserver { dirName ->
                binding.folderNameText.text = dirName
            }

            addWillGeoTrackWalkObserver { willTrack ->
                binding.recWalkSwitch.isChecked = willTrack
                if (willTrack) {
                    adjustRecIconsVisibility(binding.walkRecIcon, binding.walkRecIconDisabled)
                }
            }
            addWillGeoTrackRunObserver { willTrack ->
                binding.recRunSwitch.isChecked = willTrack
                if (willTrack) {
                    adjustRecIconsVisibility(binding.runRecIcon, binding.runRecIconDisabled)
                }
            }
            addWillGeoTrackBikeObserver { willTrack ->
                binding.recBikeSwitch.isChecked = willTrack
                if (willTrack) {
                    adjustRecIconsVisibility(binding.bikeRecIcon, binding.bikeRecIconDisabled)
                }
            }
            addWillGeoTrackVehicleObserver { willTrack ->
                binding.recVehicleSwitch.isChecked = willTrack
                if (willTrack) {
                    adjustRecIconsVisibility(binding.vehicleRecIcon, binding.vehicleRecIconDisabled)
                }
            }

            walkText.addObserver {
                try {
                    binding.lastWalkText.text = it
                } catch (e: NullPointerException) {
                    //log.d { "FIX ME - happens when opening settings" }
                }
            }

            runText.addObserver {
                try {
                    binding.lastRunText.text = it
                } catch (e: NullPointerException) {
                    //log.d { "FIX ME - happens when opening settings" }
                }
            }

            bikeText.addObserver {
                try {
                    binding.lastBikeText.text = it
                } catch (e: NullPointerException) {
                    //log.d { "FIX ME - happens when opening settings" }
                }
            }

            vehicleText.addObserver {
                try {
                    binding.lastVehicleText.text = it
                } catch (e: NullPointerException) {
                    //log.d { "FIX ME - happens when opening settings" }
                }
            }
        }

        binding.apply {
            recWalkSwitch.setOnCheckedChangeListener { _, newState ->
                viewModel.onUserActivityGeoTrackingSwitched(
                    UserActivityTrackingRepository.UserActivity.WALK, newState
                )
            }
            recRunSwitch.setOnCheckedChangeListener { _, newState ->
                viewModel.onUserActivityGeoTrackingSwitched(
                    UserActivityTrackingRepository.UserActivity.RUN, newState
                )
            }
            recBikeSwitch.setOnCheckedChangeListener{ _, newState ->
                viewModel.onUserActivityGeoTrackingSwitched(
                    UserActivityTrackingRepository.UserActivity.BIKE, newState
                )
            }
            recVehicleSwitch.setOnCheckedChangeListener { _, newState ->
                viewModel.onUserActivityGeoTrackingSwitched(
                    UserActivityTrackingRepository.UserActivity.VEHICLE, newState
                )
            }
        }

        return binding.root
    }

    private fun adjustRecIconsVisibility(recIcon: ImageView, recIconDisabled: ImageView) {
        if (recIconDisabled.visibility == View.VISIBLE) {
            recIconDisabled.visibility = View.INVISIBLE
            recIcon.visibility = View.VISIBLE
        }
    }
}
