import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


# Function to compute the magnitude of acceleration
def compute_acceleration_magnitude(ax, ay, az):
    """
    Compute the magnitude of the acceleration vector.
    This is calculated using the formula:
    |a| = sqrt(ax^2 + ay^2 + az^2)
    
    Parameters:
    ax (float): Acceleration in the x-direction.
    ay (float): Acceleration in the y-direction.
    az (float): Acceleration in the z-direction.
    
    Returns:
    float: Magnitude of the acceleration.
    """
    return np.sqrt(ax**2 + ay**2 + az**2)

# Function to compute the magnitude of angular velocity
def compute_angular_magnitude(wx, wy, wz):
    """
    Compute the magnitude of the angular velocity vector.
    This is calculated using the formula:
    |w| = sqrt(wx^2 + wy^2 + wz^2)
    
    Parameters:
    wx (float): Angular velocity around the x-axis.
    wy (float): Angular velocity around the y-axis.
    wz (float): Angular velocity around the z-axis.
    
    Returns:
    float: Magnitude of the angular velocity.
    """
    return np.sqrt(wx**2 + wy**2 + wz**2)

# Function to compute the pitch angle
def compute_pitch(ax, ay, az):
    """
    Compute the pitch angle (rotation around the x-axis).
    The pitch is calculated using:
    theta_pitch = arctan2(ay, sqrt(ax^2 + az^2))
    
    Parameters:
    ax (float): Acceleration in the x-direction.
    ay (float): Acceleration in the y-direction.
    az (float): Acceleration in the z-direction.
    
    Returns:
    float: Pitch angle in radians.
    """
    return np.arctan2(ay, np.sqrt(ax**2 + az**2))

# Function to compute the roll angle
def compute_roll(ax, az):
    """
    Compute the roll angle (rotation around the y-axis).
    The roll is calculated using:
    theta_roll = arctan2(-ax, az)
    
    Parameters:
    ax (float): Acceleration in the x-direction.
    az (float): Acceleration in the z-direction.
    
    Returns:
    float: Roll angle in radians.
    """
    return np.arctan2(-ax, az)

# Function to compute the jerk (rate of change of acceleration)
def compute_jerk(a_prev, a_curr, delta_t):
    """
    Compute the jerk, which is the rate of change of acceleration.
    The jerk is calculated using the formula:
    jerk = (a_curr - a_prev) / delta_t
    
    Parameters:
    a_prev (float): Acceleration at the previous time step.
    a_curr (float): Acceleration at the current time step.
    delta_t (float): Time interval between the two accelerations.
    
    Returns:
    float: Jerk value.
    """
    return (a_curr - a_prev) / delta_t

def plot_sensor_data(data, save_path=None):
    """
    Plot sensor data from IMU readings
    
    Args:
        data (pd.DataFrame): DataFrame containing sensor data
        save_path (str, optional): Path to save the plot. If None, display plot
        
    Returns:
        fig: matplotlib figure object
    """
    # Create time array
    time = np.arange(len(data))
    
    # Create 3x1 subplot
    fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(15, 15))
    
    # 1. Linear Acceleration Components
    ax1.plot(time, data['linear_acceleration[0]'], label='X', color='r')
    ax1.plot(time, data['linear_acceleration[1]'], label='Y', color='g')
    ax1.plot(time, data['linear_acceleration[2]'], label='Z', color='b')
    ax1.set_title('Linear Acceleration Components')
    ax1.set_ylabel('Acceleration (m/s²)')
    ax1.legend()
    ax1.grid(True)
    
    # 2. Acceleration Magnitude
    ax2.plot(time, data['acceleration_magnitude'], color='purple')
    ax2.set_title('Acceleration Magnitude')
    ax2.set_ylabel('Magnitude (m/s²)')
    ax2.grid(True)
    
    # 3. Orientation Components
    ax3.plot(time, data['absolute_orientation[0]'], label='Roll', color='r')
    ax3.plot(time, data['absolute_orientation[1]'], label='Pitch', color='g')
    ax3.plot(time, data['absolute_orientation[2]'], label='Yaw', color='b')
    ax3.set_title('Absolute Orientation Components')
    ax3.set_xlabel('Time')
    ax3.set_ylabel('Orientation (rad)')
    ax3.legend()
    ax3.grid(True)
    
    # Adjust layout
    plt.tight_layout()
    
    # Save if path provided
    if save_path:
        plt.savefig(save_path)
        plt.close()
    
    # Print statistics
    print("\nBasic Statistics:")
    stats_columns = ['acceleration_magnitude']
    print(data[stats_columns].describe())




def crop_dataset(file_path, start_index, fall_value=0, save_path=None):
    """
    Crop dataset from a specific starting index and add fall feature
    
    Args:
        file_path (str): Path to the original CSV file
        start_index (int): Index to start from
        fall_value (int): Value for the fall feature (default 0)
        save_path (str, optional): Path to save the cropped CSV. If None, won't save
    
    Returns:
        pd.DataFrame: Cropped dataset with fall feature
    """
    # Read the CSV file
    df = pd.read_csv(file_path)
    
    # Crop from start_index
    cropped_df = df.iloc[start_index:]
    
    # Add fall feature
    cropped_df['fall'] = fall_value
    
    # Reset index
    cropped_df = cropped_df.reset_index(drop=True)
    
    # Save if path provided
    if save_path:
        cropped_df.to_csv(save_path, index=False)
        print(f"Saved cropped dataset to {save_path}")
    
    print(f"Original dataset length: {len(df)}")
    print(f"Cropped dataset length: {len(cropped_df)}")
    print(f"Fall value set to: {fall_value}")
    
    return cropped_df

# Usage examples:
# For normal movement (fall = 0)
# cropped_data = crop_dataset('normal_session.csv', start_index=30)

# For fall movement (fall = 1)
# cropped_data = crop_dataset('fall_session.csv', start_index=30, fall_value=1)

    """cropped_data = crop_dataset(
        file_path='sessions/normal/S17.csv',
        start_index=40,
        save_path='dataset/normal/normal8.csv'
    )
    """
    
    
def combine_sessions(file_paths):
    """
    Combine multiple CSV sessions into one dataset with session IDs
    
    Args:
        file_paths (list): List of paths to CSV files
    
    Returns:
        pd.DataFrame: Combined dataset with session IDs
    """
    combined_data = []
    
    for session_id, file_path in enumerate(file_paths):
        # Read the CSV
        df = pd.read_csv(file_path)
        
        # Add session identifier
        df['session_id'] = session_id
        df['fall'] = 0
        
        combined_data.append(df)
    
    # Combine all sessions
    combined_df = pd.concat(combined_data, ignore_index=True)
    
    return combined_df

    """
    # Example usage:
training_files = [
    'dataset/normal/normal1.csv',
    'dataset/normal/normal2.csv',
    'dataset/normal/normal3.csv',
    'dataset/normal/normal4.csv',
    'dataset/normal/normal5.csv',
    'dataset/normal/normal6.csv',
    'dataset/normal/normal7.csv',
    'dataset/normal/normal8.csv'
]

# Combine all sessions
combined_dataset = combine_sessions(training_files)

# Save combined dataset
combined_dataset.to_csv('dataset/combined_training_data.csv', index=False)
    """



def calculateJM(data, sampling_rate=20, save_path=None):
    """
    Process sensor data to calculate additional features
    
    Args:
        data (pd.DataFrame): DataFrame containing sensor data
        sampling_rate (float): Sampling rate of the sensor in Hz
        save_path (str, optional): Path to save processed data
        
    Returns:
        pd.DataFrame: Processed data with additional features
    """
    # Define required columns
    required_columns = [
        'linear_acceleration[0]', 'linear_acceleration[1]', 'linear_acceleration[2]',
        'angular_velocity[0]', 'angular_velocity[1]', 'angular_velocity[2]'
    ]
    
    features_to_drop = ['temperature', 'humidity', 'pressure', 'altitude']
    # Check for missing columns
    missing_columns = [col for col in required_columns if col not in data.columns]
    
    if missing_columns:
        print("\nWarning: Missing these required columns:")
        print(missing_columns)
        return None
        
    # Make a copy to avoid modifying original data
    processed_data = data.copy()
    
    # Replace NaN values with 0
    processed_data = processed_data.fillna(0)
    processed_data = processed_data.drop(columns=features_to_drop)

    
    # 1. Acceleration magnitude
    processed_data['acceleration_magnitude'] = np.sqrt(
        processed_data['linear_acceleration[0]']**2 + 
        processed_data['linear_acceleration[1]']**2 + 
        processed_data['linear_acceleration[2]']**2
    )
    
    # 2. Angular velocity magnitude
    processed_data['angular_velocity_magnitude'] = np.sqrt(
        processed_data['angular_velocity[0]']**2 +
        processed_data['angular_velocity[1]']**2 +
        processed_data['angular_velocity[2]']**2
    )
    
    # 3. Pitch angle
    processed_data['pitch'] = np.arctan2(
        processed_data['linear_acceleration[1]'],
        np.sqrt(processed_data['linear_acceleration[0]']**2 +
               processed_data['linear_acceleration[2]']**2)
    )
    
    # 4. Roll angle
    processed_data['roll'] = np.arctan2(
        -processed_data['linear_acceleration[0]'],
        processed_data['linear_acceleration[2]']
    )
    
    # 5. Jerk calculations
    delta_t = 1/sampling_rate
    
    for i in range(3):
        acc_col = f'linear_acceleration[{i}]'
        jerk_col = f'jerk[{i}]'
        processed_data[jerk_col] = processed_data[acc_col].diff() / delta_t
    
    processed_data['jerk_magnitude'] = np.sqrt(
        processed_data['jerk[0]']**2 + 
        processed_data['jerk[1]']**2 + 
        processed_data['jerk[2]']**2
    )
    
    # Fill any new NaN values
    processed_data = processed_data.fillna(0)
    
    # Save if path provided
    if save_path:
        processed_data.to_csv(save_path, index=False)
        print(f"Saved processed data to {save_path}")
    
    return processed_data

# Example usage function
def process_session(file_path, save_processed=True):
    """
    Process a single session of sensor data
    
    Args:
        file_path (str): Path to CSV file
        save_processed (bool): Whether to save processed data
    """
    # Read data
    data = pd.read_csv(file_path)
    
    # Process data
    if save_processed:
        save_path = file_path.replace('.csv', '_processed.csv')
        processed_data = calculateJM(data, save_path=save_path)
    else:
        processed_data = calculateJM(data)
    
    return processed_data

# Usage examples:
# Single file processing
# processed_data = process_session('your_file.csv')

# Multiple files processing
# files = ['session1.csv', 'session2.csv', 'session3.csv']
# processed_sessions = [process_session(f) for f in files]


def crop_fall(file_path, start_index, fall_start, fall_end, save_path=None):
    """
    Crop dataset and add fall labels based on a range
    
    Args:
        file_path (str): Path to the original CSV file
        start_index (int): Index to start from
        fall_start (int): Start index of fall event (relative to cropped data)
        fall_end (int): End index of fall event (relative to cropped data)
        save_path (str, optional): Path to save the cropped CSV. If None, won't save
    
    Returns:
        pd.DataFrame: Cropped dataset with fall labels
    """
    # Read the CSV file
    df = pd.read_csv(file_path)
    
    # Crop from start_index
    cropped_df = df.iloc[start_index:]
    
    # Reset index
    cropped_df = cropped_df.reset_index(drop=True)
    
    # Add fall label column (initialize all to 0)
    cropped_df['fall'] = 0
    
    # Set fall=1 for the specified range
    cropped_df.loc[fall_start:fall_end, 'fall'] = 1
    
    # Save if path provided
    if save_path:
        cropped_df.to_csv(save_path, index=False)
        print(f"Saved cropped dataset to {save_path}")
    
    print(f"Original dataset length: {len(df)}")
    print(f"Cropped dataset length: {len(cropped_df)}")
    print(f"Number of fall samples: {cropped_df['fall'].sum()}")
    
    return cropped_df


def compute_all_features(data, sampling_rate=20):
    """
    Compute all IMU features from raw sensor data
    
    Parameters:
    data (pd.DataFrame): DataFrame containing sensor data with columns:
        - linear_acceleration[0,1,2]
        - angular_velocity[0,1,2]
    sampling_rate (float): Sampling rate in Hz for jerk calculation
    
    Returns:
    pd.DataFrame: DataFrame with all computed features
    """
    processed_data = data.copy()
    
    # 1. Compute acceleration magnitude
    processed_data['acceleration_magnitude'] = compute_acceleration_magnitude(
        processed_data['linear_acceleration[0]'],
        processed_data['linear_acceleration[1]'],
        processed_data['linear_acceleration[2]']
    )
    
    # 2. Compute angular velocity magnitude
    processed_data['angular_velocity_magnitude'] = compute_angular_magnitude(
        processed_data['angular_velocity[0]'],
        processed_data['angular_velocity[1]'],
        processed_data['angular_velocity[2]']
    )
    
    # 3. Compute pitch angle
    processed_data['pitch'] = compute_pitch(
        processed_data['linear_acceleration[0]'],
        processed_data['linear_acceleration[1]'],
        processed_data['linear_acceleration[2]']
    )
    
    # 4. Compute roll angle
    processed_data['roll'] = compute_roll(
        processed_data['linear_acceleration[0]'],
        processed_data['linear_acceleration[2]']
    )
    
    # 5. Compute jerk for each acceleration component
    delta_t = 1.0 / sampling_rate
    for i in range(3):
        acc_col = f'linear_acceleration[{i}]'
        jerk_col = f'jerk[{i}]'
        
        # Initialize jerk array with zeros
        jerk_values = np.zeros(len(processed_data))
        
        # Compute jerk for each point except the first
        for j in range(1, len(processed_data)):
            jerk_values[j] = compute_jerk(
                processed_data[acc_col].iloc[j-1],
                processed_data[acc_col].iloc[j],
                delta_t
            )
            
        processed_data[jerk_col] = jerk_values
    
    # 6. Compute jerk magnitude
    processed_data['jerk_magnitude'] = compute_acceleration_magnitude(
        processed_data['jerk[0]'],
        processed_data['jerk[1]'],
        processed_data['jerk[2]']
    )
    
    # Print some statistics
    print("\nFeature Statistics:")
    print("\nAcceleration Magnitude:")
    print(processed_data['acceleration_magnitude'].describe())
    print("\nAngular Velocity Magnitude:")
    print(processed_data['angular_velocity_magnitude'].describe())
    print("\nJerk Magnitude:")
    print(processed_data['jerk_magnitude'].describe())
    
    return processed_data


def organize_columns(data):
    """
    Organize DataFrame columns by dropping environmental data and reordering columns
    
    Parameters:
    data (pd.DataFrame): Input DataFrame with all columns
    
    Returns:
    pd.DataFrame: DataFrame with organized columns
    """
    # Copy the input DataFrame
    organized_data = data.copy()
    
    # Drop environmental columns
    columns_to_drop = ['temperature', 'humidity', 'pressure', 'altitude', 'session_id']
    organized_data = organized_data.drop(columns=columns_to_drop, errors='ignore')
    
    # Define desired column order
    column_order = [
        'session_id',
        'absolute_orientation[0]', 'absolute_orientation[1]', 'absolute_orientation[2]',
        'angular_velocity[0]', 'angular_velocity[1]', 'angular_velocity[2]',
        'linear_acceleration[0]', 'linear_acceleration[1]', 'linear_acceleration[2]',
        'acceleration_magnitude', 'angular_velocity_magnitude',
        'pitch', 'roll',
        'jerk[0]', 'jerk[1]', 'jerk[2]', 'jerk_magnitude',
        'fall'
    ]
    
    # Reorder columns (only include existing columns)
    existing_columns = [col for col in column_order if col in organized_data.columns]
    organized_data = organized_data[existing_columns]
    
    print("Columns dropped:", columns_to_drop)
    print("\nFinal column order:")
    print(organized_data.columns.tolist())
    
    return organized_data


def plot_acceleration_magnitude(data, save_path=None):
    """
    Plot the acceleration magnitude from IMU readings
    
    Args:
        data (pd.DataFrame): DataFrame containing sensor data
        save_path (str, optional): Path to save the plot. If None, display plot
        
    Returns:
        fig: matplotlib figure object
    """
    # Create time array
    time = np.arange(len(data))
    
    # Create figure and axis for the plot
    fig, ax = plt.subplots(figsize=(10, 5))
    
    # Plot acceleration magnitude
    ax.plot(time, data['acceleration_magnitude'], color='purple')
    ax.set_title('Acceleration Magnitude')
    ax.set_xlabel('Time')
    ax.set_ylabel('Magnitude (m/s²)')
    ax.grid(True)
    
    # Adjust layout
    plt.tight_layout()
    
    # Save if path provided
    if save_path:
        plt.savefig(save_path)
        plt.close()
    else:
        plt.show()
    
    # Print statistics
    print("\nBasic Statistics for Acceleration Magnitude:")
    print(data['acceleration_magnitude'].describe())
    
    return fig
