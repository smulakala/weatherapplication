import React, {useEffect, useRef, useState} from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import "./App.css";
const App = () => {

    const [forecasts, setForecasts] = useState(false);
    const [forecast, setForecast] = useState(false);
    const inputRef = useRef();
    const search = async (query, metric) => {
        if(query === ""){
            setForecast({message: 'Please enter Address Location for Forecast.'});
            setForecasts([]);
        }
        else {
            try {

                const url = `http://localhost:8080/forecast/api?query=${query}&metric=${metric}`;
                const response = await fetch(url);
                const data = await response.json();
                console.log(data);
                if (data.message) {
                    setForecast({
                        message: data.message
                    })
                    setForecasts([]);
                } else {

                    setForecast({
                        title: data.title,
                        currentTemperature: data.currentTemperature,
                        highTemperature: data.highTemperature,
                        lowTemperature: data.lowTemperature,
                        iconDescription: data.iconDescription,
                        iconUrl: data.iconUrl,
                        message: data.message,
                        cached: data.cached

                    })
                    setForecasts(data.forecasts);
                }
            } catch (error) {
                alert('Error Occured');
            }
        }
    }

    useEffect(() => {
        search("Folsom, CA","false");
    }, []);
    const colorStyle = {
        color: '#ddd'
    };
    const errorStyle = {
        color: 'Red'
    };
    const boxHeight = {
        height: '260px'
    };
  return (
      <section className={"vh-100"}>
          <div className={"container py-5 h-100"}>

              <div className={"row d-flex justify-content-center align-items-center h-100"}>
                  <div className={"col-md-8 col-lg-6 col-xl-4"}>
                      <p style={errorStyle}>{forecast.message}</p>

                      <h3 className={"mb-4 pb-2 fw-normal"}>Check the Weather Forecast</h3>

                      <div className={"input-group rounded mb-3"}>
                          <input ref={inputRef} type="text" className={"form-control rounded"}
                                 placeholder="Enter Address or City, State or Zipcode to check forecast"
                                 aria-label="Search"
                                 aria-describedby="search-addon"/>
                          <a href="#!" onClick={() => search(inputRef.current.value, "false")} type="button">
                            <span className={"input-group-text border-0 fw-bold"} id="search-addon">
                              Check!
                            </span>
                          </a>
                      </div>

                      <div className={"card shadow-0 border"}>
                          <div className={"card-body p-4"}>

                              <h4 className={"mb-1 sfw-normal"}>{forecast.title} {forecast.cached === "true" &&
                                  <label style={errorStyle}>O</label>
                              }</h4>
                              <p className={"mb-2"}>Current
                                  temperature: <strong>{forecast.currentTemperature}°F</strong></p>

                              <p>Max: <strong>{forecast.highTemperature}°F</strong>,
                                  Min: <strong>{forecast.lowTemperature}°F</strong></p>

                              <div className={"d-flex flex-row align-items-center"}>
                                  <p className={"mb-0 me-4"}>{forecast.iconDescription}</p>
                                  <img
                                      src={forecast.iconUrl}
                                      alt="night_clear"
                                      height="100"
                                  />
                              </div>

                          </div>
                      </div>

                  </div>

                  <div className={"d-flex justify-content-around text-center align-items-center px-5 bg-body-tertiary"}
                       style={boxHeight}>
                      {Array.from(forecasts).map(item => (
                          <div className={"flex-column"}>
                              <p>Max: <strong>{item.highTemperature}°F</strong></p>
                              <p>Min: <strong>{item.lowTemperature}°F</strong></p>
                              <p className={"mb-0"}><strong>{item.skyDescription}</strong></p>
                              <img src={item.iconLink}/>
                              <p className={"mb-0"}><strong>{item.weekday}</strong></p>
                          </div>
                      ))}

                  </div>

              </div>

          </div>

      </section>
  );
}

export default App;
