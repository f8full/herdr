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

    override fun routeToDriveEdit() {
        this.findNavController().navigate(R.id.action_herdrFragment_to_driveEditFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        viewModel.addUserActivityObserver { newUserActivity ->
            // reset all tints
            binding.stillImageView.setSvgColor(R.color.black)
            binding.walkImageView.setSvgColor(R.color.black)
            binding.runImageView.setSvgColor(R.color.black)
            binding.bikeImageView.setSvgColor(R.color.black)
            binding.vehicleImageView.setSvgColor(R.color.black)

            // reset all switches
            binding.recWalkSwitch.isEnabled = true
            binding.recRunSwitch.isEnabled = true
            binding.recBikeSwitch.isEnabled = true
            binding.recVehicleSwitch.isEnabled = true

            when(newUserActivity) {
                UserActivityTrackingRepository.UserActivity.STILL ->
                    binding.stillImageView.setSvgColor(R.color.theme_accent)

                UserActivityTrackingRepository.UserActivity.WALK -> {
                    binding.walkImageView.setSvgColor(R.color.theme_accent)
                    binding.recWalkSwitch.isEnabled = false
                }
                UserActivityTrackingRepository.UserActivity.RUN -> {
                    binding.runImageView.setSvgColor(R.color.theme_accent)
                    binding.recRunSwitch.isEnabled = false
                }
                UserActivityTrackingRepository.UserActivity.BIKE -> {
                    binding.bikeImageView.setSvgColor(R.color.theme_accent)
                    binding.recBikeSwitch.isEnabled = false
                }
                UserActivityTrackingRepository.UserActivity.VEHICLE -> {
                    binding.vehicleImageView.setSvgColor(R.color.theme_accent)
                    binding.recVehicleSwitch.isEnabled = false
                }
            }
        }

        viewModel.apply {
            addWillGeoTrackWalkObserver { willTrack ->
                binding.recWalkSwitch.isChecked = willTrack
            }
            addWillGeoTrackRunObserver { willTrack ->
                binding.recRunSwitch.isChecked = willTrack
            }
            addWillGeoTrackBikeObserver { willTrack ->
                binding.recBikeSwitch.isChecked = willTrack
            }
            addWillGeoTrackVehicleObserver { willTrack ->
                binding.recVehicleSwitch.isChecked = willTrack
            }

            walkText.addObserver {
                binding.lastWalkText.text = it
            }

            runText.addObserver {
                binding.lastRunText.text = it
            }

            bikeText.addObserver {
                binding.lastBikeText.text = it
            }

            vehicleText.addObserver {
                binding.lastVehicleText.text = it
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
            recVehicleSwitch.setOnCheckedChangeListener{ _, newState ->
                viewModel.onUserActivityGeoTrackingSwitched(
                    UserActivityTrackingRepository.UserActivity.VEHICLE, newState
                )
            }
        }

        return binding.root
    }
}
