import './App.css';
import MainPage from "./MainPage";
import UploadPage from './UploadPage';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

function App() {
  return (
    <div className="App">
        <Router>
            <Switch>
                <Route path='/' exact={true} component={MainPage}/>
                <Route path='/upload' exact={true} component={UploadPage}/>
            </Switch>
        </Router>
    </div>
  );
}

export default App;
